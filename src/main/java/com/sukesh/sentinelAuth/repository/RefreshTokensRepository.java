package com.sukesh.sentinelAuth.repository;

import com.sukesh.sentinelAuth.entity.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokensRepository extends JpaRepository<RefreshTokens,Long> {


}
