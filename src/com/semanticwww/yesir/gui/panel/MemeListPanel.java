package com.semanticwww.yesir.gui.panel ;

import java.util.Collections ;
import java.util.Comparator ;
import java.util.Vector ;

import java.awt.BorderLayout ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTable ;
import javax.swing.ListSelectionModel ;
import javax.swing.table.DefaultTableModel ;
import javax.swing.table.JTableHeader ;
import javax.swing.table.TableColumnModel ;

import com.semanticwww.yesir.gateway.Meme ;
import com.semanticwww.yesir.gui.YesirGUI ;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Jie Bao
 * @version 1.0
 */

public class MemeListPanel
    extends JPanel
{

    YesirGUI parentWin ;
    MemeTable jInputTable = new MemeTable() ;

    public MemeListPanel( YesirGUI parent )
    {
        this.parentWin = parent ;
        try
        {
            jbInit() ;
        }
        catch( Exception ex )
        {
            ex.printStackTrace() ;
        }
    }

    void jbInit()
        throws Exception
    {
        this.setLayout( new BorderLayout() ) ;
        this.add( new JScrollPane( jInputTable ) , BorderLayout.CENTER ) ;

    }

    public void updateList( Meme meme[] )
    {
        jInputTable.removeAll() ;
        if( meme == null )
        {
            return ;
        }
        for( int i = 0 ; i < meme.length ; i++ )
        {
            Meme m = ( Meme ) meme[i].clone() ;
            m.concise = true ;

            String date ;
            if( m.getDate() == null )
            {
                date = "" ;
            }
            else
            {
                date = m.getDate().toString() ;

            }
            try
            {
                jInputTable.addRow( new Object[]
                                    {new Boolean( false ) ,
                                    m ,
                                    date} ) ;
            }
            catch( Exception ex )
            {
                ex.printStackTrace() ;
            }

        }
    }

    class MemeTable
        extends JTable
    {
        String[] Column = new String[]
                          {"Selected" , "Subject" , "Date" , "Status"} ;
        String[] ColumnType = new String[]
                              {"java.lang.Boolean" ,
                              "com.semanticwww.yesir.gateway.Meme" ,
                              "java.lang.String" ,
                              "java.lang.String"} ;

        private MyTableModel tableModel ;
        private JTableHeader header ;
        private boolean[] sortAscending ;

        public class ColumnHeaderListener
            extends MouseAdapter
        {
            public void mouseClicked( MouseEvent evt )
            {
                JTable table = ( ( JTableHeader ) evt.getSource() ).getTable() ;
                TableColumnModel colModel = table.getColumnModel() ;

                // The index of the column whose header was clicked
                int vColIndex = colModel.getColumnIndexAtX( evt.getX() ) ;
                int mColIndex = table.convertColumnIndexToModel( vColIndex ) ;

                // Return if not clicked on any column header
                if( vColIndex == -1 )
                {
                    return ;
                }
                sortAllRowsBy( mColIndex , sortAscending[mColIndex] ) ;
                sortAscending[mColIndex] = !sortAscending[mColIndex] ;
            }
        }

        public class MyTableModel
            extends DefaultTableModel
        {
            public MyTableModel()
            {
                super() ;
            }

            // These methods always need to be implemented.
            public int getColumnCount()
            {
                return Column.length ;
            }

//                public int getRowCount() { return data.length;}

            // The default implementations of these methods in
            // AbstractTableModel would work, but we can refine them.
            public String getColumnName( int column )
            {
                return Column[column] ;
            }

            public Class getColumnClass( int col )
            {
                try
                {
                    return Class.forName( ColumnType[col] ) ;
                }
                catch( ClassNotFoundException ex )
                {
                    ex.printStackTrace() ;
                    return null ;
                }

            }

            public boolean isCellEditable( int row , int col )
            {
//                return col == 0 ;
                return false ;
            }
        }

//    TableSorter  sorter ;

        public MemeTable()
        {
            // Create a model of the data.
            tableModel = new MyTableModel() ;
            this.setModel( tableModel ) ;
            jbInit() ;
        }

        /**
         * @author Jie Bao
         * @version 2003-10-29
         */
        private void jbInit()
        {
            sortAscending = new boolean[Column.length] ;
            for( int i = 0 ; i < Column.length ; i++ )
            {
                tableModel.addColumn( Column[i] ) ;
                sortAscending[i] = true ;
            }
            setEnabled( true ) ;
            setRowSelectionAllowed( true ) ;
            setColumnSelectionAllowed( false ) ;
            setCellSelectionEnabled( false ) ;
            this.getSelectionModel().setSelectionMode( ListSelectionModel.
                SINGLE_SELECTION ) ;
            this.addMouseListener( new MouseAdapter()
            {
                public void mouseClicked( MouseEvent e )
                {
                    parentWin.updateMemePreview( getSelected() ) ;
                }
            } ) ;

            header = getTableHeader() ;
            header.addMouseListener( new ColumnHeaderListener() ) ;

        }

        public void removeAll()
        {
            int rows = tableModel.getRowCount() ;
            for( int i = 0 ; i < rows ; i++ )
            {
                tableModel.removeRow( 0 ) ;
            }
        }

        public void addRow( Object strList[] )
        {
//        Debug.trace( "string lenth" + strList.length ) ;
            setRowSelectionAllowed( true ) ;
            tableModel.addRow( strList ) ;
        }

        public void sortAllRowsBy( int colIndex ,
                                   boolean ascending )
        {
            Vector data = tableModel.getDataVector() ;
            ColumnSorter sorter = new ColumnSorter( colIndex , ascending ) ;
            Collections.sort( data , sorter ) ;
            tableModel.fireTableStructureChanged() ;
        }

        /**
         *
         * @return selected ID  - single selection
         * @author Jie Bao
         * @version 2003-11-02
         */
        public Meme getSelected()
        {
            if( !getColumnSelectionAllowed() && getRowSelectionAllowed() )
            {
                // Row selection is enabled
                // Get the indices of the selected rows
                int[] rowIndices = getSelectedRows() ;

//            Debug.trace(""+rowIndices.length);
                if( rowIndices.length == 1 )
                {
                    return( Meme ) tableModel.getValueAt( rowIndices[0] , 1 ) ;
                }
                else
                {
                    return null ;
                }
            }

            return null ;
        }

    }
}

/**
 * This comparator is used to sort vectors of data
 *
 */
class ColumnSorter
    implements Comparator
{
    int colIndex ;
    boolean ascending ;
    ColumnSorter( int colIndex , boolean ascending )
    {
        this.colIndex = colIndex ;
        this.ascending = ascending ;
    }

    public int compare( Object a , Object b )
    {
        Vector v1 = ( Vector ) a ;
        Vector v2 = ( Vector ) b ;
        Object o1 = v1.get( colIndex ) ;
        Object o2 = v2.get( colIndex ) ;

        // Treat empty strains like nulls
        if( o1 instanceof String && ( ( String ) o1 ).length() == 0 )
        {
            o1 = null ;
        }
        if( o2 instanceof String && ( ( String ) o2 ).length() == 0 )
        {
            o2 = null ;
        }

        // Sort nulls so they appear last, regardless
        // of sort order
        if( o1 == null && o2 == null )
        {
            return 0 ;
        }
        else if( o1 == null )
        {
            return 1 ;
        }
        else if( o2 == null )
        {
            return -1 ;
        }
        else if( o1 instanceof Comparable )
        {
            if( ascending )
            {
                return( ( Comparable ) o1 ).compareTo( o2 ) ;
            }
            else
            {
                return( ( Comparable ) o2 ).compareTo( o1 ) ;
            }
        }
        else
        {
            if( ascending )
            {
                return o1.toString().compareTo( o2.toString() ) ;
            }
            else
            {
                return o2.toString().compareTo( o1.toString() ) ;
            }
        }
    }
}
