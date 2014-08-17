package cloudify.widget.common;

import org.apache.commons.io.DirectoryWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/17/14
 * Time: 6:18 PM
 */
public class ResourceWalker extends DirectoryWalker<ResourceWalker.ResourceWalkResult> {

    private static Logger logger = LoggerFactory.getLogger(ResourceWalker.class);
    protected ResourceWalker() {
        super( null, 3 ); // lets support only 3 levels of depth.

    }

    Stack<ResourceWalkResult> memory = new Stack<ResourceWalkResult>();
    File rootFile = null;

    private boolean isSymbolicLink( File file ){
        try {
            File canon;
            if (file.getParent() == null) {
                canon = file;
            } else {
                File canonDir = file.getParentFile().getCanonicalFile();
                canon = new File(canonDir, file.getName());
            }
            return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
        }catch(Exception e){
            logger.error("unable to decide if file is symbolic link",e);

        }
        return true; // better safe than sorry
    }

    @Override
    protected File[] filterDirectoryContents(File directory, int depth, File[] files) throws IOException {
        List<File> result = new LinkedList<File>();

        for (File file : files) {
            if ( !file.isHidden() && !isSymbolicLink(file) ){
                result.add(file);
            }
        }

        return result.toArray(new File[result.size()]);

    }

    public void walkResource( File base, Collection<ResourceWalkResult> files )  {
        rootFile = base;
        try {
            super.walk(base, files);
        }catch(Exception e ){
            throw new RuntimeException("unable to walk resource",e);
        }
    }

    @Override
    protected void handleFile(File file, int depth, Collection<ResourceWalkResult> results) throws IOException {
        ResourceWalkResult newFile = new ResourceWalkResult();
        newFile.name = file.getName();
        newFile.path = rootFile.toURI().relativize(new File(file.getCanonicalPath()).toURI()).getPath();
        memory.peek().addChild(newFile);
        super.handleFile(file, depth, results);
    }

    @Override
    protected void handleDirectoryStart(File directory, int depth, Collection<ResourceWalkResult> results) throws IOException {
        ResourceWalkResult newDirectory = new ResourceWalkResult();
        newDirectory.isDirectory = true;
        if ( memory.size() > 0 ) {
            newDirectory.name = directory.getName();
            memory.peek().addChild(newDirectory);
        }else{
            newDirectory.name = "root";
            results.add(newDirectory);
        }
        memory.push(newDirectory);
        super.handleDirectoryStart(directory, depth, results);
    }

    @Override
    protected void handleDirectoryEnd(File directory, int depth, Collection<ResourceWalkResult> results) throws IOException {
        memory.pop();
        super.handleDirectoryEnd(directory, depth, results);
    }



    public static class ResourceWalkResult {
        public String name;
        public boolean isDirectory = false;
        public String path;
        public List<ResourceWalkResult> children = null;

        @Override
        public String toString() {
            return "ResourceWalkResult{" +
                    "name='" + name + '\'' +
                    ", children=" + children +
                    '}';
        }

        public void addChild( ResourceWalkResult entry ){
            if ( children == null ){
                children = new LinkedList<ResourceWalkResult>();
            }
            children.add( entry );
        }
    }
}
