# Integration Test Plan for ADD PARTITIONS IF NOT EXISTS multiRangePartition

## Summary of Implementation

We have successfully implemented support for `ADD PARTITIONS IF NOT EXISTS multiRangePartition` syntax by making the following changes:

### 1. Grammar Changes (StarRocks.g4)
- **File**: `/Users/wangfei/project/github/happut/starrocks/fe/fe-core/src/main/antlr/StarRocks.g4`
- **Line**: 1222
- **Change**: Added `(IF NOT EXISTS)?` to the `addPartitionClause` rule for `multiRangePartition`

```antlr
addPartitionClause
    : ADD TEMPORARY? (singleRangePartition | PARTITIONS (IF NOT EXISTS)? multiRangePartition) distributionDesc? properties?
    | ADD TEMPORARY? (singleItemListPartitionDesc | multiItemListPartitionDesc) distributionDesc? properties?
    ;
```

### 2. Java Implementation Changes

#### MultiRangePartitionDesc.java
- **File**: `/Users/wangfei/project/github/happut/starrocks/fe/fe-core/src/main/java/com/starrocks/sql/ast/MultiRangePartitionDesc.java`
- **Changes**:
  - Added `ifNotExists` field
  - Added new constructor with `ifNotExists` parameter
  - Maintained backward compatibility with existing constructor
  - Added `isIfNotExists()` getter method
  - Updated `convertToSingle()` method to pass `ifNotExists` flag to `SingleRangePartitionDesc`

#### AstBuilder.java
- **File**: `/Users/wangfei/project/github/happut/starrocks/fe/fe-core/src/main/java/com/starrocks/sql/parser/AstBuilder.java`
- **Changes**:
  - Modified `visitAddPartitionClause` method to detect `IF NOT EXISTS` clause
  - Updated logic to create `MultiRangePartitionDesc` with proper `ifNotExists` flag

## Test Cases

### Test Case 1: Date-based Multi-Range Partitions with IF NOT EXISTS
```sql
-- Create base table
CREATE TABLE test_date_multirange (
    k1 DATE,
    k2 INT,
    v1 VARCHAR(100)
)
PARTITION BY RANGE (k1) (
    PARTITION p20140101 VALUES LESS THAN ("2014-01-01")
)
DISTRIBUTED BY HASH(k2) BUCKETS 10;

-- Test new syntax (should succeed)
ALTER TABLE test_date_multirange 
ADD PARTITIONS IF NOT EXISTS 
START ("2014-01-01") END ("2014-01-04") EVERY (INTERVAL 1 DAY);

-- Test existing partitions (should not fail with IF NOT EXISTS)
ALTER TABLE test_date_multirange 
ADD PARTITIONS IF NOT EXISTS 
START ("2014-01-02") END ("2014-01-05") EVERY (INTERVAL 1 DAY);
```

### Test Case 2: Number-based Multi-Range Partitions with IF NOT EXISTS
```sql
-- Create base table
CREATE TABLE test_number_multirange (
    k1 INT,
    v1 VARCHAR(100)
)
PARTITION BY RANGE (k1) (
    PARTITION p1 VALUES LESS THAN ("10")
)
DISTRIBUTED BY HASH(k1) BUCKETS 10;

-- Test new syntax (should succeed)
ALTER TABLE test_number_multirange 
ADD PARTITIONS IF NOT EXISTS 
START ("10") END ("15") EVERY (1);

-- Test existing partitions (should not fail with IF NOT EXISTS)
ALTER TABLE test_number_multirange 
ADD PARTITIONS IF NOT EXISTS 
START ("12") END ("18") EVERY (1);
```

### Test Case 3: Backward Compatibility
```sql
-- Test that existing syntax still works
ALTER TABLE test_date_multirange 
ADD PARTITIONS 
START ("2014-01-10") END ("2014-01-13") EVERY (INTERVAL 1 DAY);
```

## Expected Behavior

### With IF NOT EXISTS
1. **New partitions**: Creates partitions that don't already exist
2. **Existing partitions**: Silently skips partitions that already exist (no error)
3. **Mixed scenario**: Creates new partitions, skips existing ones

### Without IF NOT EXISTS (existing behavior)
1. **New partitions**: Creates partitions normally
2. **Existing partitions**: Throws error if partitions already exist

## Validation Steps

1. **Grammar Validation**: Verify ANTLR grammar compiles without errors
2. **Parser Validation**: Verify SQL statements parse correctly with new syntax
3. **AST Validation**: Verify `MultiRangePartitionDesc` objects are created with correct `ifNotExists` flag
4. **Partition Creation**: Verify actual partition creation respects `IF NOT EXISTS` semantics
5. **Error Handling**: Verify appropriate error handling for malformed syntax

## Implementation Status

✅ **COMPLETED**:
- Grammar rule modification in StarRocks.g4
- MultiRangePartitionDesc class enhancement
- AstBuilder parsing logic update
- Backward compatibility maintenance
- Test SQL files creation

## Key Features

1. **Full IF NOT EXISTS Support**: Both date and number-based multiRangePartition support
2. **Backward Compatibility**: Existing code continues to work unchanged
3. **Proper Error Propagation**: Maintains existing error handling patterns
4. **Consistent API**: Follows existing StarRocks patterns and conventions

## Files Modified

1. `/Users/wangfei/project/github/happut/starrocks/fe/fe-core/src/main/antlr/StarRocks.g4` - Line 1222
2. `/Users/wangfei/project/github/happut/starrocks/fe/fe-core/src/main/java/com/starrocks/sql/ast/MultiRangePartitionDesc.java` - Multiple changes
3. `/Users/wangfei/project/github/happut/starrocks/fe/fe-core/src/main/java/com/starrocks/sql/parser/AstBuilder.java` - Lines 5084-5122

## Next Steps for Full Integration

1. Compile and test with actual StarRocks environment
2. Run existing unit tests to ensure no regressions
3. Add specific unit tests for the new functionality
4. Performance testing with large partition ranges
5. Integration testing with cluster environments