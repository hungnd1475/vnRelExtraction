package vn.edu.hcmut.emrre.machineLearningMethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.FeatureNode;
import de.bwaldvogel.liblinear.InvalidInputDataException;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;

public class SVM {

    private String fileModel;
    private int dimension;
    private List<double[]> data;
    
    public SVM(String fileModel, int dimension, List<double[]> data) {
        this.fileModel = fileModel;
        this.dimension = dimension;
        this.data = data;
    }
    
    public SVM(String fileModel) {
        this.fileModel = fileModel;
    }

    public String getFileModel() {
        return fileModel;
    }

    public void setModel(String fileModel) {
        this.fileModel = fileModel;
    }

    public int getDimesion() {
        return dimension;
    }

    public void setDimesion(int dimesion) {
        this.dimension = dimesion;
    }

    public List<double[]> getData() {
        return data;
    }

    public void setData(List<double[]> data) {
        this.data = data;
    }

    private Feature[] parse2FeatureNode(double[] vector, boolean sparseVector) {
        List<Feature> arrayLst = new ArrayList<Feature>();
        int bound = vector.length/2;
        for (int i = 0; i < bound; i++) {
            if (sparseVector){
                if (i < bound)
                    arrayLst.add(new FeatureNode((int)vector[i], vector[i + bound]));
                else
                    arrayLst.add(new FeatureNode((int)vector[i], 1));
            }
            else
                if (vector[i] != -1) {
                    arrayLst.add(new FeatureNode(i + 1, vector[i]));
                }
        }
        Feature[] result = new FeatureNode[arrayLst.size()];
        for (int i = 0; i < arrayLst.size(); i++) {
           result[i] = arrayLst.get(i);
        }
        return result;
    }
    
    public void svmTrainCore() throws IOException {
        Problem svm = new Problem();
        svm.l = data.size();
        svm.n = dimension;
        

        Feature[][] feature = new FeatureNode[svm.l][];
        double[] label = new double[svm.l];

        for (int i = 0; i < svm.l; i++) {
            feature[i] = parse2FeatureNode(data.get(i), true);
            label[i] = data.get(i)[data.get(i).length - 1];
        }

        svm.x = feature;
        svm.y = label;
        SolverType solver = SolverType.MCSVM_CS; // -s 0
        double C = 0.5; // cost of constraints violation
        double eps = 0.5; // stopping criteria

        Parameter parameter = new Parameter(solver, C, eps);
        Model model = Linear.train(svm, parameter);
        File fileModel = new File(this.fileModel);
        model.save(fileModel);
    }
    
    public void svmTrainCore(File trainDataFile) throws IOException, InvalidInputDataException {
        Problem svm = Problem.readFromFile(trainDataFile, 0);
        SolverType solver = SolverType.L2R_LR_DUAL; // -s 0
        double C = 0.5; // cost of constraints violation
        double eps = 0.5; // stopping criteria
        Parameter parameter = new Parameter(solver, C, eps);
        Model model = Linear.train(svm, parameter);
        File fileModel = new File(this.fileModel);
        model.save(fileModel);
    }

    public double svmTestCore(double[] dataTest, boolean sparseVector) throws IOException {
        File fileModel = new File(this.fileModel);
        Model model = Model.load(fileModel);
        Feature[] instance = parse2FeatureNode(dataTest, sparseVector);
        double prediction = Linear.predict(model, instance);
        return prediction;
    }
    
}
