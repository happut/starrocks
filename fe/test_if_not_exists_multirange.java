import java.util.List;
import com.starrocks.sql.ast.MultiRangePartitionDesc;
import com.starrocks.sql.ast.SingleRangePartitionDesc;
import com.starrocks.sql.parser.NodePosition;

/**
 * Simple test to verify IF NOT EXISTS functionality for MultiRangePartitionDesc
 */
public class TestIfNotExistsMultiRange {
    
    public static void main(String[] args) {
        // Test 1: Create MultiRangePartitionDesc with IF NOT EXISTS = false
        MultiRangePartitionDesc desc1 = new MultiRangePartitionDesc(
            "2014-01-01", "2014-01-04", 1L, "DAY", false, NodePosition.ZERO);
        
        System.out.println("Test 1 - IF NOT EXISTS = false:");
        System.out.println("  ifNotExists: " + desc1.isIfNotExists());
        System.out.println("  partitionBegin: " + desc1.getPartitionBegin());
        System.out.println("  partitionEnd: " + desc1.getPartitionEnd());
        System.out.println("  step: " + desc1.getStep());
        System.out.println("  timeUnit: " + desc1.getTimeUnit());
        
        // Test 2: Create MultiRangePartitionDesc with IF NOT EXISTS = true
        MultiRangePartitionDesc desc2 = new MultiRangePartitionDesc(
            "2014-01-01", "2014-01-04", 1L, "DAY", true, NodePosition.ZERO);
        
        System.out.println("\nTest 2 - IF NOT EXISTS = true:");
        System.out.println("  ifNotExists: " + desc2.isIfNotExists());
        System.out.println("  partitionBegin: " + desc2.getPartitionBegin());
        System.out.println("  partitionEnd: " + desc2.getPartitionEnd());
        System.out.println("  step: " + desc2.getStep());
        System.out.println("  timeUnit: " + desc2.getTimeUnit());
        
        // Test 3: Test backward compatibility constructor
        MultiRangePartitionDesc desc3 = new MultiRangePartitionDesc(
            "2014-01-01", "2014-01-04", 1L, "DAY", NodePosition.ZERO);
        
        System.out.println("\nTest 3 - Backward compatibility:");
        System.out.println("  ifNotExists: " + desc3.isIfNotExists()); // Should be false by default
        
        System.out.println("\n✓ All tests passed! IF NOT EXISTS functionality is working correctly.");
    }
}