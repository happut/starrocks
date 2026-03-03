-- Test SQL for ADD PARTITIONS IF NOT EXISTS multiRangePartition functionality
-- This tests our implementation of the new IF NOT EXISTS syntax

CREATE TABLE test_multi_range_partition (
    k1 DATE,
    k2 INT,
    k3 SMALLINT,
    v1 VARCHAR(2048)
)
ENGINE=olap
DUPLICATE KEY(k1, k2, k3)
PARTITION BY RANGE (k1) (
    PARTITION p20140101 VALUES LESS THAN ("2014-01-01"),
    PARTITION p20140102 VALUES LESS THAN ("2014-01-02")
)
DISTRIBUTED BY HASH(k2) BUCKETS 10
PROPERTIES (
    "replication_num" = "1"
);

-- Test 1: ADD PARTITIONS with multiRangePartition (without IF NOT EXISTS)
ALTER TABLE test_multi_range_partition 
ADD PARTITIONS 
START ("2014-01-03") END ("2014-01-06") EVERY (INTERVAL 1 DAY);

-- Test 2: ADD PARTITIONS IF NOT EXISTS with multiRangePartition (our new functionality)
ALTER TABLE test_multi_range_partition 
ADD PARTITIONS IF NOT EXISTS 
START ("2014-01-05") END ("2014-01-08") EVERY (INTERVAL 1 DAY);

-- Test 3: ADD PARTITIONS IF NOT EXISTS for number partitions
CREATE TABLE test_multi_range_partition_number (
    k2 INT,
    k3 SMALLINT,
    v1 VARCHAR(2048)
)
ENGINE=olap
DUPLICATE KEY(k2, k3)
PARTITION BY RANGE (k2) (
    PARTITION p1 VALUES LESS THAN ("2"),
    PARTITION p2 VALUES LESS THAN ("3")
)
DISTRIBUTED BY HASH(k2) BUCKETS 10
PROPERTIES (
    "replication_num" = "1"
);

ALTER TABLE test_multi_range_partition_number 
ADD PARTITIONS IF NOT EXISTS 
START ("2") END ("5") EVERY (1);

-- Clean up
DROP TABLE test_multi_range_partition;
DROP TABLE test_multi_range_partition_number;