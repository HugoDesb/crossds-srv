package io.github.hugodesb.matelist.repository;

import io.github.hugodesb.matelist.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
