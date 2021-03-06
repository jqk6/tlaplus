// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.
// Last modified on Mon 30 Apr 2007 at 13:42:27 PST by lamport
//      modified on Thu Nov 16 15:53:30 PST 2000 by yuanyu

package tlc2.value.impl;

import java.util.Enumeration;

import tlc2.util.Vect;
import tlc2.value.IMVPerm;
import tlc2.value.IValue;
import util.Assert;
import util.Set;

public abstract class MVPerms {
  
  public static final IMVPerm[] permutationSubgroup(final Enumerable enumerable) {
    final ValueEnumeration Enum = enumerable.elements();
	final int sz = enumerable.size() - 1;
    final Set perms = new Set(sz);
    final Vect<IMVPerm> permVec = new Vect<>(sz);
    // Compute the group generators:
    Value elem;
    while ((elem = Enum.nextElement()) != null) {
      final FcnRcdValue fcn = (FcnRcdValue) elem.toFcnRcd();
      if (fcn == null) {
	Assert.fail("The symmetry operator must specify a set of functions.");
      }
      final IMVPerm perm = new MVPerm();
      for (int i = 0; i < fcn.domain.length; i++) {
	final IValue dval = fcn.domain[i];
	final IValue rval = fcn.values[i];
	if ((dval instanceof ModelValue) && (rval instanceof ModelValue)) {
	  perm.put((ModelValue)dval, (ModelValue)rval);
	}
	else {
	  Assert.fail("Symmetry function must have model values as domain and range.");
	}
      }
      if (perm.size() > 0 && perms.put(perm) == null) {
	permVec.addElement(perm);
      }
    }
    // Compute the group generated by the generators:
    final int gsz = permVec.size();
    int sz0 = 0;
    while (true) {
      final int sz1 = permVec.size();
      for (int i = 0; i < gsz; i++) {
	final IMVPerm perm1 = (IMVPerm)permVec.elementAt(i);
	for (int j = sz0; j < sz1; j++) {
		IMVPerm perm = perm1.compose((IMVPerm)permVec.elementAt(j));
	  if (perm.size() > 0 && perms.put(perm) == null) {
	    permVec.addElement(perm);
	  }
	}
      }
      if (sz1 == permVec.size()) { break; }
      sz0 = sz1;
    }
    // Finally, put all the elements in an array ready for use:
    final IMVPerm[] res = new IMVPerm[permVec.size()];
    final Enumeration<IMVPerm> permEnum = permVec.elements();
    for (int i = 0; i < res.length; i++) {
      res[i] = permEnum.nextElement();
    }
    return res;
  }
}
