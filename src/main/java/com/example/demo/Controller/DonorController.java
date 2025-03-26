package com.example.demo.Controller;

import java.util.*;

import com.example.demo.Entity.BloodDetails;
import com.example.demo.Entity.Donor;

import com.example.demo.Entity.User;
import com.example.demo.Service.DonorService;
import com.example.demo.Service.UserService;
import com.example.demo.Entity.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "https://blood-donation-frontended-8.onrender.com")
public class DonorController
{
    @Autowired
    private DonorService donorService;

    @Autowired
    private UserService registrationService;

    @PostMapping("/addDonor")
    public Donor addNewDonor(@RequestBody Donor donor) throws Exception
    {
        return donorService.saveDonor(donor);
    }

    @PostMapping("/addAsDonor")
    public Donor addUserAsDonor(@RequestBody Donor donor) throws Exception
    {
        return donorService.saveUserAsDonor(donor);
    }

    @GetMapping("/acceptstatus/{email}")
    public ResponseEntity<List<String>> updateStatus(@PathVariable String email) throws Exception
    {
        donorService.updateStatus(email);
        List<String> al=new ArrayList<>();
        al.add("accepted");
        return new ResponseEntity<List<String>>(al,HttpStatus.OK);
    }

    @GetMapping("/rejectstatus/{email}")
    public ResponseEntity<List<String>> rejectStatus(@PathVariable String email) throws Exception
    {
        donorService.rejectStatus(email);
        List<String> al=new ArrayList<>();
        al.add("rejected");
        return new ResponseEntity<List<String>>(al,HttpStatus.OK);
    }

    @GetMapping("/donorlist")
    @CrossOrigin(origins = "https://blood-donation-frontended-8.onrender.com")
    public ResponseEntity<List<Donor>> getDonors() throws Exception
    {
        List<Donor> donors = donorService.getAllDonors();
        return new ResponseEntity<List<Donor>>(donors, HttpStatus.OK);
    }// Ensure correct import

    @GetMapping("/requestHistory")
    public ResponseEntity<List<Request>> getRequestHistory() {
        List<Request> history = donorService.getRequestHistory();
        return new ResponseEntity<>(history, HttpStatus.OK);
    }



    @GetMapping("/requestHistory/{email}")
    public ResponseEntity<List<Request>> getRequestHistoryByEmail(@PathVariable String email) {
        List<Request> history = donorService.getRequestHistoryByEmail(email);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    @GetMapping("/bloodDetails")
    @CrossOrigin(origins = "https://blood-donation-frontended-8.onrender.com")
    public ResponseEntity<List<BloodDetails>> getBloodDetails() throws Exception
    {
        List<Donor> bloodDetails = donorService.getBloodDetails();

        List<Donor> donors = donorService.getAllDonors();
        donorService.checkforOldBloodSamples(donors);

        List<String> groups = new ArrayList<>();
        List<Integer> units = new ArrayList<>();
        Map<String, Integer> details = new LinkedHashMap<>();
        for(Donor donor:bloodDetails)
        {
            if(details.containsKey(donor.getBloodgroup()))
                details.put(donor.getBloodgroup(), details.get(donor.getBloodgroup())+1);
            else
                details.put(donor.getBloodgroup(), 1);
            if(groups.contains(donor.getBloodgroup()))
            {
                int index = groups.indexOf(donor.getBloodgroup());
                units.set(index,units.get(index)+donor.getUnits());
            }
            else
            {
                groups.add(donor.getBloodgroup());
                units.add(donor.getUnits());
            }
        }
        List<BloodDetails> result = new ArrayList<>();
        for(Map.Entry<String, Integer> m:details.entrySet())
        {
            result.add(new BloodDetails(m.getKey(),m.getValue(),units.get(0)));
            units.remove(0);
        }
        return new ResponseEntity<List<BloodDetails>>(result, HttpStatus.OK);
    }

    @GetMapping("/getTotalUsers")
    public ResponseEntity<List<Integer>> getTotalUsers() throws Exception
    {
        List<User> users = registrationService.getAllUsers();
        List<Integer> al=new ArrayList<>();
        al.add(users.size());
        return new ResponseEntity<List<Integer>>(al,HttpStatus.OK);
    }

    @GetMapping("/getTotalDonors")
    public ResponseEntity<List<Integer>> getTotalDonors() throws Exception
    {
        List<Donor> donors = donorService.getAllDonors();

        donorService.checkforOldBloodSamples(donors);

        List<Integer> al=new ArrayList<>();
        al.add(donors.size());
        return new ResponseEntity<List<Integer>>(al,HttpStatus.OK);
    }

    @GetMapping("/getTotalBloodGroups")
    public ResponseEntity<List<Integer>> getTotalBloodGroups() throws Exception
    {
        List<Donor> bloodDetails = donorService.getBloodDetails();
        Set<String> set = new LinkedHashSet<>();
        for(Donor details:bloodDetails)
        {
            set.add(details.getBloodgroup());
        }
        List<Integer> al=new ArrayList<>();
        al.add(set.size());
        return new ResponseEntity<List<Integer>>(al,HttpStatus.OK);
    }

    @GetMapping("/getTotalUnits")
    @CrossOrigin(origins = "https://blood-donation-frontended-8.onrender.com")
    public ResponseEntity<List<Integer>> getTotalUnits() throws Exception
    {
        List<Donor> bloodDetails = donorService.getBloodDetails();
        int units = 0;
        for(Donor details:bloodDetails)
        {
            units += details.getUnits();
        }
        List<Integer> al=new ArrayList<>();
        al.add(units);
        return new ResponseEntity<List<Integer>>(al,HttpStatus.OK);
    }

    @GetMapping("/getTotalRequests/{email}")
    public ResponseEntity<Integer> getTotalRequests(@PathVariable String email) {
        int totalRequests = donorService.getTotalRequestsByEmail(email);
        return new ResponseEntity<>(totalRequests, HttpStatus.OK);
    }

    @GetMapping("/getTotalDonationCount/{email}")
    @CrossOrigin(origins = "https://blood-donation-frontended-8.onrender.com")
    public ResponseEntity<List<Integer>> getTotalDonationCount(@PathVariable String email) throws Exception
    {
        List<Donor> donors = donorService.getAllDonors();
        List<Integer> al = new ArrayList<>();
        int count = 0;
        for(Donor val:donors)
        {
            if(val.getName().equalsIgnoreCase("gowtham"))
                count++;
        }
        al.add(count);
        return new ResponseEntity<List<Integer>>(al, HttpStatus.OK);
    }

    @GetMapping("/userlist")
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = registrationService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/getUserProfile/{email}")
    @CrossOrigin(origins = "https://blood-donation-frontended-8.onrender.com")
    public ResponseEntity<User> getUserProfile(@PathVariable String email) {
        User user = registrationService.getUserByEmail(email.trim());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }







    @PostMapping("/requestblood")
    public ResponseEntity<Request> addNewBloodRequest(@RequestBody Request request) {
        Request savedRequest = donorService.saveBloodRequest(request);
        return new ResponseEntity<>(savedRequest, HttpStatus.CREATED);
    }
}
