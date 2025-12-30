package com.vbs.demo.controller;

import com.vbs.demo.dto.DisplayDto;
import com.vbs.demo.dto.Logindto;
import com.vbs.demo.dto.UpdateDto;
import com.vbs.demo.models.History;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.HistoryRepo;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    UserRepo userRepo;
    @Autowired
    HistoryRepo historyRepo;
    @PostMapping("/register")//trigerd aya jo bhi function likha vo excute hoga
    public String register(@RequestBody User user) //return string esliye public string
    {
        userRepo.save(user);
        History h1 = new History();
        h1.setDescription("User Self Created :"+user.getUsername());
        historyRepo.save(h1);
        return "Signup Successful";
    }
    @PostMapping("/login")
    public String login(@RequestBody Logindto u)
    {
        User user = userRepo.findByUsername(u.getUsername());
        if(user == null)
        {
            return "User Not Found";
        }
        if(!u.getPassword().equals(user.getPassword()))
        {
            return "password Incorrect";
        }
        if(!u.getRole().equals(user.getRole()))
        {
            return "Role Incorrect";
        }
        return String.valueOf(user.getId());
    }
    @GetMapping("/get-details/{id}")   //function banya
    public DisplayDto display(@PathVariable int id)  //primary key here is IDor specific hai
    {
        User user = userRepo.findById(id).orElseThrow(()->new RuntimeException("user is not found"));
        DisplayDto displayDto = new DisplayDto();
        displayDto.setUsername(user.getUsername());//dispaydto jo hai class mei userneme hai vo private hai esliye setter and getter ka use hota hai
        displayDto.setBalance(user.getBalance());//why use get becoz it changes every time
        return displayDto; //object ko return kiya kyuki object mei username and balance hai

    }
    @PostMapping("/update")
    public String update (@RequestBody UpdateDto obj)
    {
        User user = userRepo.findById(obj.getId()).orElseThrow(()->new RuntimeException("Not found"));
        History h1 = new History();
        if(obj.getKey().equalsIgnoreCase("name"))
        {
            if(user.getName().equals(obj.getValue())) return "Cannot Be Same";
            h1.setDescription("User changed from : "+user.getUsername()+ " to "+obj.getValue());
            user.setName(obj.getValue());
        } else if (obj.getKey().equalsIgnoreCase("password")) {
            if(user.getPassword().equals(obj.getValue())) return "Cannot Be Same";
            h1.setDescription("User changed password : "+user.getPassword());
            user.setPassword(obj.getValue());

        } else if (obj.getKey().equalsIgnoreCase("email")) {
            if(user.getEmail().equals(obj.getValue())) return "Cannot Be Same";

            User user2 = userRepo.findByEmail(obj.getValue());
            if(user2 !=null) return "Email Already Exists ";
            user.setEmail(obj.getValue());
            h1.setDescription("User changed Email from : "+user.getEmail()+ " to "+obj.getValue());

        }
        else{
            return "Invalid key";
        }
        historyRepo.save(h1);
        userRepo.save(user);
        return "updated Successfully";
    }
    @PostMapping("/add/{adminId}")
    public String add(@RequestBody User user,@PathVariable int adminId)
    {
        History h1 = new History();
        h1.setDescription("User "+user.getUsername()+" Created By admin :"+adminId);
        historyRepo.save(h1);
        userRepo.save(user);
        return "Added Successfully";
    }

    @GetMapping("/users/{keyword}")
    public List<User> getUsers(@PathVariable String keyword)
    {
        return userRepo.findByUsernameContainingIgnoreCaseAndRole(keyword,"customer");
    }
    @DeleteMapping("/delete-user/{userId}/admin/{adminId}")
    public String deleteUser(@PathVariable int userId,@PathVariable int adminId)
    {
        History h1 = new History();
        User user = userRepo.findById(userId).orElseThrow(()->new RuntimeException("Not found"));
        if(user.getBalance()>0)
        {
            return "Balance should be Zero";
        }
        h1.setDescription("User "+user.getUsername()+" Deleted successfully By admin :"+adminId);
        historyRepo.save(h1);
        userRepo.delete(user);
        return "user Deleted Sucessfully";
    }


}
