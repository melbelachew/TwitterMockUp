package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listTweets(Model model){
        model.addAttribute("tweets", tweetRepository.findAll());
        return "tweets";
    }
    @ModelAttribute
    LocalDateTime initLocalDate() {
        return LocalDateTime.now();
    }

    @GetMapping("/add")
    public String tweetform(Model model){
        model.addAttribute("tweet", new Tweet());
        model.addAttribute("localDateTime", LocalDateTime.now());
        return "tweetform";
    }

    @PostMapping("/process")
    public String processForm(
                              @ModelAttribute Tweet tweet,
                              @ModelAttribute LocalDateTime localDateTime,

                              @Valid @RequestParam("file") MultipartFile file, BindingResult result){
        if(file.isEmpty()){
            return "redirect:/";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            tweet.setHeadShot(uploadResult.get("url").toString());
            tweetRepository.save(tweet);

        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }

        if(result.hasErrors()){
            return "tweetform";
        }
        tweetRepository.save(tweet);
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showTweet(@PathVariable("id") long id, Model model){
        model.addAttribute("tweet", tweetRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateTweet(@PathVariable("id") long id, Model model){
        model.addAttribute("tweet", tweetRepository.findById(id).get());
        return "tweetform";
    }

    @RequestMapping("/delete/{id}")
    public String delTweet(@PathVariable("id") long id){
        tweetRepository.deleteById(id);
        return "redirect:/";
    }
}
