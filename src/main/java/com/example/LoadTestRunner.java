//package com.example;
//
//import com.example.entity.AppUser;
//import com.example.service.UserService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadFactory;
//
//@Component
//@Order(2)
//@Slf4j
//public class LoadTestRunner implements CommandLineRunner {
//
//    private final UserService userService;
//
//
//    public LoadTestRunner(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public void run(String... args) {
//        System.out.println("LoadTestRunner starting ...");
//        ThreadFactory factory = Thread.ofVirtual().name("VIRTUAL-WORKER-", 0L).factory();
//
//        try (ExecutorService e = Executors.newThreadPerTaskExecutor(factory)) {
//            for (int i = 0; i < 1000; i++) {
//                final int t = i;
//                e.submit(() -> {
//                    long start = System.currentTimeMillis();
//                    try {
//                        AppUser appUser = new AppUser("user" + UUID.randomUUID().toString() + "@gmail.com", "Password@123", "Password@123", "firstName", "lastName");
//                        userService.createUser(appUser);
//                        long end = System.currentTimeMillis();
//                        System.out.println(Thread.currentThread() + "  " + t + "Created user in " + (end - start) + " ms");
//                    } catch (Exception ex) {
//                        log.error(ex.getMessage(), ex);
//                    }
//                });
//            }
//        }
//    }
//
//}
