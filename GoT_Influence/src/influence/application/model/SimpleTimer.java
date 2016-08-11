package influence.application.model;

/**
 * helper class for measuring execution time
 * @author Muidinov Aider
 *
 */
public class SimpleTimer {
	private Long start;
	private Long end;
	private Long result;
	
	public SimpleTimer() {
		this.start = 0l;
		this.end = 0l;
	}
	
	public void start(String message) {
		this.start = System.currentTimeMillis();
		if (message != null) {
			System.out.print(message);
		}
	}
	
	public void finish(String message) {
		this.end = System.currentTimeMillis();
		this.result = this.end - this.start;
		if (message != null) {
			System.out.println(message+" ("+secResult()+" sec.)");
		}
	}
	
	public Float secResult() {
		Float res = (float) (this.result)/1000;
		return res;
	}
	
}
