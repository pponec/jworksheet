/**
  * Copyright (C) 2007-2021, Pavel Ponec, contact: http://ponec.net/
  *
  * This program is free software; you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation version 2 of the License.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You may obtain a copy of the License at
  * http://www.gnu.org/licenses/gpl-2.0.txt
  */


package net.ponec.jworksheet.gui.models;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.ujorm.Ujo;

/**
 * UjoComboBox Model
 * @author Pavel Ponec
 */
public class UjoComboBoxModel extends DefaultComboBoxModel {
    
    protected List<Ujo> ujos;
    //final protected Key property;
    
    protected int pointer = -1;
    
    /**
     * Creates a new instance of UjoComboBoxModel
     */
    public UjoComboBoxModel() {
        this(new ArrayList<Ujo>(0));
    }
    
    /** Creates a new instance of UjoComboBoxModel */
    @SuppressWarnings("unchecked")
    public UjoComboBoxModel(List ujos) {
        this.ujos = ujos;
    }
    
    @Override
    public void setSelectedItem(Object anItem) {
        for (pointer=0; pointer<ujos.size(); pointer++) {
            if (equals(anItem, ujos.get(pointer))) {
                return;
            }
        }
//        if (anItem instanceof Ujo) {
//            Ujo ujo = (Ujo) anItem;
//            Key[] props = ujo.readKeys();
//            for(Key pro : props) {
//                for (pointer=0; pointer<ujos.size(); pointer++) {
//                    if (equals(pro.of(ujo), pro.of((Ujo)ujos.get(pointer)))) {
//                        return;
//                    }
//                }
//            }
//        }
        pointer = -1;
    }
    
    /** is equals */
    protected boolean equals(Object obj1, Object obj2) {
        if (obj1==null) { return obj2==null; }
        if (obj2==null) { return false; }
        return obj1.equals(obj2);
    }
    
    @Override
    public Object getSelectedItem() {
        return pointer>=0 && pointer<ujos.size() ? ujos.get(pointer) : null ;
    }
    
    @Override
    public Object getElementAt(int index) {
        return ujos.get(index);
    }
    
    @Override
    public int getSize() {
        return ujos.size();
    }
    
    /** Set Ujo Data */
    public void setData(List<Ujo> data) {
        this.ujos = data;
    }
    
}
