package com.fingerprint.repositories;


import com.fingerprint.model.Finger;
import com.fingerprint.model.FingerPrint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FingerPrintRepository extends JpaRepository<FingerPrint, String> {
    Optional<FingerPrint> findByUserIdAndFinger(String userId, Finger fingerEnum);

}
