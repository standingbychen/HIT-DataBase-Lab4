/**
 * 
 */
package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author chen
 *
 */
public class Buffer {
    private int ioCounter=0;
    private int bufferSize;
    private int blockSize;
    private int blockTotalNumber;
    private int blockFreeNumber;
    
    /** 存储数据 */
    public List<Block> blocks = new ArrayList<>();
    
    
    protected Buffer(int bufferSize, int blockSize) {
        super();
        this.bufferSize = bufferSize;
        this.blockSize = blockSize;
        this.blockTotalNumber = bufferSize / blockSize+1;
        this.blockFreeNumber = blockTotalNumber;
        
        // 初始化block 
        for(int i=0;i<blockTotalNumber;i++) {
            blocks.add(new Block(i,blockSize));
        }
    }
    
    /**
     * 释放缓冲
     */
    public void free() {
        for (Block block : blocks) {
            block.free=true;
        }
    }
    
    /**
     * 获得块
     * @return 新块索引index
     */
    public Block getNewBlockInBuffer() {
        if (blockFreeNumber==0) {
            return null;
        }
        for(int i=0;i<blockTotalNumber;i++) {
            if (blocks.get(i).free) {
                blocks.get(i).free=false;
                blockFreeNumber--;
                return blocks.get(i);
            }
        }
        return null;
    }
    
    public void freeBlockInBuffer(int index) {
        if (!blocks.get(index).free) {
            blocks.get(index).free=true;
            blockFreeNumber++;
        }
    }
    
    
    public Block readBlockFromDisk(int addr) {
        if (blockFreeNumber==0) {
            System.err.println("Buffer Overflows!");
            return null;
        }
        Block block=null;
        int index=-1;
        for(int i=0;i<blockTotalNumber;i++) {
            if (blocks.get(i).free) {
                index = i;
                block=blocks.get(i);
                break;
            }
        }
        if (block==null) { // 无空块
            return null;
        }
        // 读取文件内容到block
        String filename= ExtMem.DISKADDR+addr+".blk";
        File file = new File(filename);
        try {
            List<String> strings = Files.readAllLines(Paths.get(filename));
            //  parse to block
            String[] line = strings.get(0).split(",");
            for(int i=0;i<line.length;i++) {
                block.data[i] = Integer.valueOf(line[i]);
            }
            block.free=false;
            blockFreeNumber--;
            return block;
        } catch (IOException e) {
            System.err.println("Reading Block Failed! :"+filename);
            e.printStackTrace();
        }
        return null;
    }
    
    
    public boolean writeBlockToDisk(int index , int addr) {
        String filename= ExtMem.DISKADDR+addr+".blk";
        File file = new File(filename);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(blocks.get(index).toString());     
            return true;
        } catch (IOException e) {
            System.err.println("Reading Block Failed! :"+filename);
            e.printStackTrace();
            return false;
        }
    }
    
}


