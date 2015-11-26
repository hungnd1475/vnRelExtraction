package vn.edu.hcmut.emrre.spring.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import vn.edu.hcmut.emrre.core.preprocess.ProcessText;
import vn.edu.hcmut.emrre.core.preprocess.ProcessVNText;

@RestController
@RequestMapping(value = "/vn-extractor", produces = "application/json; charset=utf-8", headers = "Accept=application/json")
public class VNExtractorController {
    private ProcessText processText = ProcessVNText.getInstance();

    @RequestMapping(value = "preprocess", method = RequestMethod.POST)
    public @ResponseBody Object preProcess(@RequestBody Map<String, Object> req) {
        Map<String, List<String>> res = new HashMap<>();
        res.put("sentences", processText.preProcessDoc(req.get("record").toString()));
        return res;
    }

    @RequestMapping(value = "manual", method = RequestMethod.POST)
    public @ResponseBody Object extractRelation(@RequestBody Map<String, Object> req){
        Map<String, List<String>> res = new HashMap<>();
        return res;
    }

    @RequestMapping(value = "automatic", method = RequestMethod.POST)
    public @ResponseBody Object autoExtract(@RequestBody Map<String, Object> req){
        String record = req.get("record").toString();
        Map<String, Object> res = new HashMap<>();
        res.put("message","OK");
        return res;
    }
}
