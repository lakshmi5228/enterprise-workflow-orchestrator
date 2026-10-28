package com.yottaflex.orchestrator.repository;

import com.yottaflex.orchestrator.model.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {

    Optional<Asset> findByAssetTag(String assetTag);

    List<Asset> findBySerialNumber(String serialNumber);

    List<Asset> findByAssignedToEmployeeId(String employeeId);

    List<Asset> findByStatus(String status);

    List<Asset> findByAssetType(String assetType);

    Page<Asset> findByStatusContainingIgnoreCaseOrAssetTypeContainingIgnoreCase(
            String status, String assetType, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.status = 'IN_STOCK'")
    long countInStockAssets();

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.status IN ('ASSIGNED', 'IN_TRANSIT', 'DELIVERED', 'ACKNOWLEDGED')")
    long countDeployedAssets();

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.status = 'RETURN_REQUESTED'")
    long countReturnRequestedAssets();

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.status = 'RECOVERED'")
    long countRecoveredAssets();
}

