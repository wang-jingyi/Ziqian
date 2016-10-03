package io.github.wang_jingyi.ZiQian.active;

import java.util.List;

public class SwatState {
	
	private List<Integer> sensorValues;
	
	public SwatState(List<Integer> sv) {
		this.sensorValues = sv;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sensorValues == null) ? 0 : sensorValues.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SwatState other = (SwatState) obj;
		if (sensorValues == null) {
			if (other.sensorValues != null)
				return false;
		} else if (!sensorValues.equals(other.sensorValues))
			return false;
		return true;
	}

	public List<Integer> getSensorValues() {
		return sensorValues;
	}

	public int getIndexInStatePool(SwatStatePool ssp){
		return ssp.getSwatStateMap().get(this);
	}
	
	
	
}
