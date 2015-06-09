package com.rowland.utility;

import android.database.Cursor;

import java.util.Iterator;

/**
 * Helps mange my cursor in retrieval of data Issue on
 * <p>
 * <a>http://stackoverflow.com/questions/10723770/whats-the-best-way-to-iterate-an-android-cursor</a>
 *
 * @author Rowland
 *
 */
public class IterableCursor implements Iterable<Cursor>, Iterator<Cursor>{

	Cursor cursor;
    int toVisit;
    public IterableCursor(Cursor cursor)
    {
        this.cursor = cursor;
        toVisit = cursor.getCount();
    }
    public Iterator<Cursor> iterator()
    {
        cursor.moveToPosition(-1);
        return this;
    }
    public boolean hasNext()
    {
        return toVisit>0;
    }
    public Cursor next()
    {
    //  if (!hasNext()) {
    //      throw new NoSuchElementException();
    //  }
        cursor.moveToNext();
        toVisit--;
        return cursor;
    }
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

}
