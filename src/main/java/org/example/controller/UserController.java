//package org.example.controller;
//
//
//import org.example.dto.ResponseDTO;
//import org.example.dto.UserDTO;
//import org.example.entity.User;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//@RestController
//@RequestMapping("/users")
//@Validated
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/{id}")
//    public ResponseDTO<User> getUserById(@PathVariable Long id) {
//        User user = userService.getUserById(id);
//        return ResponseDTO.success(user);
//    }
//
//    @GetMapping("/username/{username}")
//    public ResponseDTO<User> getUserByUsername(@PathVariable String username) {
//        User user = userService.getUserByUsername(username);
//        return ResponseDTO.success(user);
//    }
//
//    @GetMapping
//    public ResponseDTO<List<User>> getAllUsers() {
//        List<User> users = userService.getAllUsers();
//        return ResponseDTO.success(users);
//    }
//
//    @PostMapping
//    public ResponseDTO<Integer> createUser(@RequestBody UserDTO userDTO) {
//        int result = userService.createUser(userDTO);
//        return ResponseDTO.success("创建成功", result);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseDTO<Integer> updateUser(@PathVariable Long id,
//                                           @RequestBody UserDTO userDTO) {
//        int result = userService.updateUser(id, userDTO);
//        return ResponseDTO.success("更新成功", result);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseDTO<Integer> deleteUser(@PathVariable Long id) {
//        int result = userService.deleteUser(id);
//        return ResponseDTO.success("删除成功", result);
//    }
//
//    @GetMapping("/search")
//    public ResponseDTO<List<User>> searchUsers(
//            @RequestParam(required = false) String username,
//            @RequestParam(required = false) Integer status) {
//        List<User> users = userService.searchUsers(username, status);
//        return ResponseDTO.success(users);
//    }
//}
