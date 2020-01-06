package com.cn.lp;

import java.io.Serializable;

public interface Func1<P, R> extends Serializable {

    R call(P p);

}
