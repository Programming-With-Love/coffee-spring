package site.zido.demo.service;

import site.zido.demo.entity.Admin;
import site.zido.demo.entity.User;
import site.zido.demo.pojo.params.UserParams;

import java.util.List;

public interface IUserService {
    void addUser(UserParams userParams, Admin opUser);

    List<User> getUsers(List<Integer> ids);
}
