package io.github.hugodesb.matelist.repository;

import io.github.hugodesb.matelist.model.Account;
import io.github.hugodesb.matelist.model.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findOneByPlatformAndUsername(Platform platform, String username);
}
