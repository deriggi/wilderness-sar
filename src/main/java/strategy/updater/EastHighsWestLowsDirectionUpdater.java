package strategy.updater;

import java.util.logging.Logger;
import raster.domain.agent.VectorAgent;

public class EastHighsWestLowsDirectionUpdater extends SkelatalDirectionUpdater{
	
	private final static Logger log = Logger.getLogger(EastHighsWestLowsDirectionUpdater.class.getName());

	@Override
	public String toString(){
		return "EastHighsWestLows";
	}

	
	private  HighGroundDirectionUpdater highGroundUpdater = new HighGroundDirectionUpdater();
	private  LowerGroundDirectionUpdater lowGroundUpdater = new LowerGroundDirectionUpdater();

	@Override
	public void updateDirection(double[] dxDy, VectorAgent ownerAgent){
		if(dxDy == null || dxDy.length != 2){
			return;
		}

		// if eastbound then go high
		if(dxDy[0] > 0){

			highGroundUpdater.updateDirection(dxDy, ownerAgent);

		}
		else{
			
			lowGroundUpdater.updateDirection(dxDy, ownerAgent);

		}

	}
}
