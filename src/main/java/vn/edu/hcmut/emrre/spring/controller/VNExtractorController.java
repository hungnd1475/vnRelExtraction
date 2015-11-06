package vn.edu.hcmut.emrre.spring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vn-extractor")
public class VNExtractorController {

    @RequestMapping(value = "extract", method = RequestMethod.POST)
    public @ResponseBody Object extract(@RequestParam("record") String record, @RequestParam("concepts") String concepts) {
        System.out.println(record);
        System.out.println(concepts);
        return "Chào em" + record;
    }
}
