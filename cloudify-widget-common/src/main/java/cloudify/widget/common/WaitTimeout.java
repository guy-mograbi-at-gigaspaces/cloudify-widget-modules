package cloudify.widget.common;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/11/14
 * Time: 1:25 PM
 */
public class WaitTimeout {
    public long timeout= 3*60000; // when, totally,  did i sleep enough. default to 3 minutes
    public long period = 10000; // how long should i sleep - default 10 second
    public long counter = 0;
    public Condition condition;


    public void sleep(){
        try{
            Thread.sleep(period);
            counter += period;
        }catch(Exception e){}
    }

    public boolean isTimeout(){
        return counter > timeout;
    }

    public void waitFor(){
        while ( !condition.apply() && !isTimeout() ){
            sleep();
        }

        if ( isTimeout() ){
            throw new RuntimeException("condition timed out " + condition);
        }
    }


    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public static interface Condition{
        boolean apply();
    }

}
