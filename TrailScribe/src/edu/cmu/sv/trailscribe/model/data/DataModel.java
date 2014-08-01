/* 
 * Copyright (c) 2014, TrailScribe Team.
 * This content is released under the MIT License. See the file named LICENSE for details.
 */
package edu.cmu.sv.trailscribe.model.data;

public interface DataModel {
    
    /**
     * @return a description or an identifier of the object.
     * <p>
     * For example, name or id
     */
    public String toString();
    
    /**
     * @return the object in Json format.
     * <p>
     * For example, "'id':'0', 'userId':'0'"
     */
    public String toJson();
}
