package ml.common.property.deducedproperties;

import api.contracts.IStorePackage;
import ml.deducer.deductionrules.ADeductionRule;

/**
 * This class represent the conclusion: you must get rid of the containing package
 * 
 * @author noam
 * 
 */
public class MustGetRidOfPackageProperty extends ADeducedProperty {

	IStorePackage storePackage;
	
	double urgency;

	public MustGetRidOfPackageProperty(IStorePackage storePackage, double urgency, ADeductionRule deducer) {
		super(deducer);
		this.storePackage = storePackage;
		this.urgency = urgency;
	}
	
	public MustGetRidOfPackageProperty(IStorePackage storePackage, double urgency) {
		this.storePackage = storePackage;
		this.urgency = urgency;
	}

	public IStorePackage getStorePackage() {
		return storePackage;
	}

	public double getUrgency() {
		return urgency;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + ((storePackage == null) ? 0 : storePackage.hashCode());
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!super.equals(o) || getClass() != o.getClass())
			return false;
		MustGetRidOfPackageProperty other = (MustGetRidOfPackageProperty) o;
		if (storePackage == null) {
			if (other.storePackage != null)
				return false;
		} else if (!storePackage.equals(other.storePackage))
			return false;
		return true;
	}

	@Override
	public String getDescription() {
		return "The packge of product: " + storePackage.getProduct().getName() + " (barcode: " + storePackage.getProduct().getBarcode() + ")" +
				" with exp. date: " + storePackage.getExpirationDate() +
				" must gets rid off the store";
	}
	
}
