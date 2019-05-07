/**
 * 
 */
package bplustree;

/**
 * @author standingby
 *
 */
public class Reference {
    /** ���̿��ַ */
    public final int block;
    /** ���̿������������� data[index] = key */
    public final int index;

    public Reference(int block, int index) {
        super();
        this.block = block;
        this.index = index;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return block + "," + index;
    }

}
