package com.example.test.controller;

import com.example.test.service.ImageService;
import com.example.test.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test/image")
public class ImageController {
    @Autowired
    private ImageService imageService;

    @GetMapping("process")
    public R task() {
        return R.ok().put(imageService.getImageTaskProcess());
    }

    @GetMapping("download")
    public R downLoadImage() {
        imageService.downloadImg();
        return R.ok();
    }
}
