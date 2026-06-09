package com.sukesh.sentinelAuth.repository;

import com.sukesh.sentinelAuth.entity.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokensRepository extends JpaRepository<RefreshTokens,Long> {

    Optional<RefreshTokens> findByToken(String token);

    @Modifying
    @Query("update RefreshTokens r set r.isUsed=true where user.userId = :id and r.isUsed=false")
    void revokeAllTokensByUserId(@Param("id") Long id);
}
