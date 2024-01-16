package com.github.valhio.storeapi;

import com.github.valhio.storeapi.model.GroceryItem;
import com.github.valhio.storeapi.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableMongoRepositories
public class StoreApiApplication implements CommandLineRunner {

    @Autowired
    ItemRepository groceryItemRepo;

    public static void main(String[] args) {
        SpringApplication.run(StoreApiApplication.class, args);


    }

    public void run(String... args) {

        System.out.println("-----CREATE GROCERY ITEMS-----\n");

        createGroceryItems();

        System.out.println("\n-----SHOW ALL GROCERY ITEMS-----\n");

        showAllGroceryItems();

        System.out.println("\n-----GET ITEM BY NAME-----\n");

        getGroceryItemByName("Whole Wheat Biscuit");

        System.out.println("\n-----GET ITEMS BY CATEGORY-----\n");

        getItemsByCategory("millets");

    //        System.out.println("\n-----UPDATE CATEGORY NAME OF SNACKS CATEGORY-----\n");
    //
    //        updateCategoryName("snacks");
    //
    //        System.out.println("\n-----DELETE A GROCERY ITEM-----\n");
    //
    //        deleteGroceryItem("Kodo Millet");

        System.out.println("\n-----FINAL COUNT OF GROCERY ITEMS-----\n");

        findCountOfGroceryItems();

        System.out.println("\n-----THANK YOU-----");

    }


    //CREATE
    void createGroceryItems() {
        System.out.println("Data creation started...");
        groceryItemRepo.save(new GroceryItem("Whole Wheat Biscuit", "Whole Wheat Biscuit", 5, "snacks"));
        groceryItemRepo.save(new GroceryItem("Kodo Millet", "XYZ Kodo Millet healthy", 2, "millets"));
        groceryItemRepo.save(new GroceryItem("Dried Red Chilli", "Dried Whole Red Chilli", 2, "spices"));
        groceryItemRepo.save(new GroceryItem("Pearl Millet", "Healthy Pearl Millet", 1, "millets"));
        groceryItemRepo.save(new GroceryItem("Cheese Crackers", "Bonny Cheese Crackers Plain", 6, "snacks"));
        System.out.println("Data creation complete...");
    }

    // READ
    // 1. Show all the data
    public void showAllGroceryItems() {

        groceryItemRepo.findAll().forEach(item -> System.out.println(getItemDetails(item)));
    }

    // 2. Get item by name
    public void getGroceryItemByName(String name) {
        System.out.println("Getting item by name: " + name);
        GroceryItem item = groceryItemRepo.findItemByName(name);
        System.out.println(getItemDetails(item));
    }

    // 3. Get name and quantity of a all items of a particular category
    public void getItemsByCategory(String category) {
        System.out.println("Getting items for the category " + category);
        List<GroceryItem> list = groceryItemRepo.findAll(category);

        list.forEach(item -> System.out.println("Name: " + item.getName() + ", Quantity: " + item.getQuantity()));
    }

    // 4. Get count of documents in the collection
    public void findCountOfGroceryItems() {
        long count = groceryItemRepo.count();
        System.out.println("Number of documents in the collection: " + count);
    }

    // Print details in readable form

    public String getItemDetails(GroceryItem item) {

        System.out.println(
                "Item Name: " + item.getName() +
                        ", \nQuantity: " + item.getQuantity() +
                        ", \nItem Category: " + item.getCategory()
        );

        return "";
    }
















    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS (Cross-Origin Resource Sharing) is a mechanism that uses additional HTTP headers to tell a browser
    // to let a web application running at one origin (domain) have permission to access selected resources
    // from a server at a different origin. A web application executes a cross-origin HTTP request when it
    // requests a resource that has a different origin (domain, protocol, or port) from its own.
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource(); // This is the source of the CORS configuration
        CorsConfiguration corsConfiguration = new CorsConfiguration(); // This is the actual CORS configuration
        corsConfiguration.setAllowCredentials(true); // This allows cookies to be sent with the CORS request
        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200")); // This allows requests from http://localhost:4200
        corsConfiguration.setAllowedHeaders(Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type",
                "Accept", "Jwt-Token", "Authorization", "Origin, Accept", "X-Requested-With",
                "Access-Control-Request-Method", "Access-Control-Request-Headers")); // This allows the following headers in the request to be sent
        corsConfiguration.setExposedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")); // This allows the following headers in the response to be exposed to the client
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // This allows the following methods to be used in the request
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration); // This registers the CORS configuration
        return new CorsFilter(urlBasedCorsConfigurationSource); // This returns the CORS filter
    }

}
