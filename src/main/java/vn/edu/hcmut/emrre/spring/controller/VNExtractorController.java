package vn.edu.hcmut.emrre.spring.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.servlet.ModelAndView;

import vn.edu.hcmut.emrre.NLP.ProcessText;
import vn.edu.hcmut.emrre.NLP.ProcessVNText;
import vn.edu.hcmut.emrre.entity.Concept;
import vn.edu.hcmut.emrre.entity.Relation;
import vn.edu.hcmut.emrre.entity.Sentence;
import vn.edu.hcmut.emrre.main.RelationExtractor;
import vn.edu.hcmut.emrre.spring.utils.HTMLHelper;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/vn-extractor", produces = "application/json; charset=utf-8", headers = "Accept=application/json")
public class VNExtractorController {
    private ProcessText processText = ProcessVNText.getInstance();
    private RelationExtractor relationCore = new RelationExtractor();

    @RequestMapping(value = "preprocess", method = RequestMethod.POST)
    public @ResponseBody Object preProcess(@RequestBody Map<String, Object> req) {
        Map<String, List<String>> res = new HashMap<>();
        res.put("sentences", processText.preProcessDoc(req.get("record").toString()));
        return res;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "manual", method = RequestMethod.POST)
    public @ResponseBody Object extractRelation(@RequestBody Map<String, Object> req) throws IOException {
        Map<String, List<String>> res = new HashMap<>();
        List<Concept> concepts = processText.parseToConcepts((List<String>) req.get("concepts"));
        List<Sentence> sentences = processText.parseToSentences((List<String>) req.get("sentences"));
        List<Relation> relations = relationCore.extractRelation(sentences, concepts);
        res.put("relations", HTMLHelper.generateRawData(relations));
        return res;
    }

    @RequestMapping(value = "automatic", method = RequestMethod.POST)
    public @ResponseBody Object autoExtract(@RequestBody Map<String, Object> req) throws IOException {
        String record = req.get("record").toString();

        List<Relation> relations = relationCore.extractRelation(record);
        List<Concept> concepts = relationCore.getConceptLstOut();
        List<Sentence> sentences = relationCore.getSentenceLstOut();

        List<String> htmlSens = HTMLHelper.generateHTMLSens(sentences, concepts);

        Map<String, Object> res = new HashMap<>();
        res.put("sentences", htmlSens);
        res.put("concepts", HTMLHelper.generateRawData(concepts));
        res.put("relations", HTMLHelper.generateRawData(relations));
        return res;
    }
}
