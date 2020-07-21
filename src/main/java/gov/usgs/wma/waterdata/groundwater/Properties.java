package gov.usgs.wma.waterdata.groundwater;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="properties")
public class Properties {
	String tier;
	String bucket;
	String region;

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
}