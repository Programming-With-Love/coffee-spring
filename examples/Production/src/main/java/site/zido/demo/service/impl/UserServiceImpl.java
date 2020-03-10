package site.zido.demo.service.impl;

import site.zido.demo.entity.Admin;
import site.zido.demo.entity.User;
import site.zido.demo.pojo.params.UserParams;
import site.zido.demo.repository.UserRepository;
import site.zido.demo.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void addUser(UserParams userParams, Admin opUser) {
        log.info("管理员：{}添加一个用户{}", opUser.getUsername(), userParams);
        userRepository.save(userParams.convertTo());
    }

    @Override
    public List<User> getUsers(List<Integer> ids) {
        return userRepository.findAllByIdIn(ids, Sort.by(DESC, "createTime"));
    }
}
