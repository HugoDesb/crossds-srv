package com.example.crossds.repository;

import com.example.crossds.business.Account;
import com.example.crossds.business.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findOneByPlatformAndUsername(Platform platform, String username);
}
