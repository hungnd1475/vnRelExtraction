package vn.edu.hcmut.emrre.spring.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import de.bwaldvogel.liblinear.InvalidInputDataException;
import vn.edu.hcmut.emrre.common.Constant;
import vn.edu.hcmut.emrre.common.ReadFile;
import vn.edu.hcmut.emrre.main.ConceptExtractor;
import vn.edu.hcmut.emrre.main.RelationExtractor;

@Controller
@RequestMapping(value = "/training/*")
public class VNTrainingController {
	private static final Logger _logger = LoggerFactory.getLogger(VNTrainingController.class);
	
	@RequestMapping(value={"/", "run"}, method=RequestMethod.GET)
	public ModelAndView training() {
        return new ModelAndView("fragments/training_run");
    }
	
	@RequestMapping(value="start", method=RequestMethod.POST)
	public @ResponseBody Object startTrain(@RequestBody Map<String, Object> req) throws IOException, InvalidInputDataException {
		String action = String.valueOf(req.get("action"));
		int err = 0;
		if ("training".equalsIgnoreCase(action)) {
			ConceptExtractor ce = new ConceptExtractor();
			ce.generateModel();
			
			RelationExtractor re = new RelationExtractor();
			re.generateModel();
		}
        Map<String, Object> rs = new HashMap<>();
        rs.put("err", err);
        return rs;
    }
	
	@RequestMapping(value="model", method=RequestMethod.GET)
	public ModelAndView viewModel() {
		Map<String, Object> rs = new HashMap<>();
		String conModel = readFileContent(Constant.CONCEPT_MODEL_FILE_PATH);
		String relModel = readFileContent(Constant.MODEL_FILE_PATH);
		
		rs.put("con_model", conModel);
		rs.put("rel_model", relModel);
		
        return new ModelAndView("fragments/training_model", rs);
    }
	
	private String readFileContent(String filePath) {
		String rs = "";
		try {
			File file = new File(filePath);
			FileReader rf = new FileReader(file);
			BufferedReader br = new BufferedReader(rf);
			String line = "";
			while ((line = br.readLine()) != null) {
				rs += line + "\n";
			}
			br.close();
		} catch (Exception e) {
			_logger.debug("Read file exception ", e);
		} 
		return rs;
	}
	
	public static void main(String[] args) {
		System.out.println(new VNTrainingController().readFileContent(Constant.CONCEPT_MODEL_FILE_PATH));
	}

}
