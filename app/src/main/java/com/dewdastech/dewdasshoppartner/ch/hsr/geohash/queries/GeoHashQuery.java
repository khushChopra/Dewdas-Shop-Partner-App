package com.dewdastech.dewdasshoppartner.ch.hsr.geohash.queries;

import java.util.List;

import com.dewdastech.dewdasshoppartner.ch.hsr.geohash.GeoHash;
import com.dewdastech.dewdasshoppartner.ch.hsr.geohash.WGS84Point;

public interface GeoHashQuery {

	/**
	 * check wether a geohash is within the hashes that make up this query.
	 */
	public boolean contains(GeoHash hash);

	/**
	 * returns whether a point lies within a query.
	 */
	public boolean contains(WGS84Point point);

	/**
	 * should return the hashes that re required to perform this search.
	 */
	public List<GeoHash> getSearchHashes();

	public String getWktBox();

}