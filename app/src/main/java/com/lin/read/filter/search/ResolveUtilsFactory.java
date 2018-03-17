package com.lin.read.filter.search;

import com.lin.read.filter.BookInfo;

import java.io.IOException;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/17.
 */

public abstract class ResolveUtilsFactory {
    public abstract List<BookInfo> getBooksByBookname(String... params) throws IOException;
}
