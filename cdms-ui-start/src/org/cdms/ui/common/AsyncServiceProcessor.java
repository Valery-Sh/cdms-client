package org.cdms.ui.common;

import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;

/**
 *
 * @author V. Shyshkin
 */
public abstract class AsyncServiceProcessor {

    protected String progressMessage;
    protected Object result;

    public AsyncServiceProcessor() {
        this("Please wait...");
    }

    public AsyncServiceProcessor(String progressMessage) {
        if (progressMessage == null) {
            this.progressMessage = "Please wait...";
        } else {
            this.progressMessage = progressMessage;
        }
    }

    public abstract Object perform();

    public Object getResult() {
        return result;
    }

    public void run(TaskListener listener) {
        RequestProcessor rp = new RequestProcessor("interruptible tasks", 1, true);
        final RequestProcessor.Task theTask;
        final ProgressHandle ph;

        Runnable runnable;
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    result = perform();
// To mimic long backgroud                    for ( int i=0; i< 20000000;i++) {
//                        int j = i +1;
//                    }
                } catch (Exception e) {
                    System.out.println("ERROR in Thread");
                    result = e;
                } finally {
                    
                }
                /*                    if (Thread.interrupted()) {
                 return;
                 }
                 */
            }
        };

        theTask = rp.create(runnable);
        ph = ProgressHandleFactory.createHandle(progressMessage, theTask);

        theTask.addTaskListener(listener);

        theTask.addTaskListener(new TaskListener() {
            @Override
            public void taskFinished(org.openide.util.Task task) {
                //Task is finished and we must get rid of the ProgressHandle
                ph.finish();
            }
        });

        //start the progresshandle the progress UI will show 500s after
        ph.start();

        //this actually start the task
        theTask.schedule(0);
    }
}
