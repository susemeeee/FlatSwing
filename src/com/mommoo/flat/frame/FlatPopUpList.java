package com.mommoo.flat.frame;

import com.mommoo.flat.frame.popup.OnItemClickListener;
import com.mommoo.flat.list.FlatListView;
import com.mommoo.flat.text.label.FlatLabel;
import com.mommoo.util.ScreenManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FlatPopUpList {
    private static final ScreenManager screenManager =ScreenManager.getInstance();
    private static final int PADDING = screenManager.dip2px(10);

    private final CommonJFrame FRAME = new CommonJFrame();
    private OnItemClickListener onItemClickListener = (index, message)->{};
    private FlatListView<FlatLabel> listView = new FlatListView<>();

    private boolean isDisposeAfterSelection;

    public FlatPopUpList(){
        initFrame();
        initContentPane();
        initListView();
        setDisposeAfterSelection(true);
        setFocusLostDispose();
        getContentPane().add(listView.getComponent());
    }

    private void initFrame(){
        FRAME.setType(Window.Type.UTILITY);
        FRAME.setShadowWidth(3);
        FRAME.setAlwaysOnTop(true);
        FRAME.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void initContentPane(){
        JPanel contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setOpaque(true);
    }

    public static void main(String[] args){
        FlatPopUpList popUpList = new FlatPopUpList();
        for (int i = 1; i < 6 ; i++){
            popUpList.addMenu("Item " + i);
        }
        popUpList.setFocusLostDispose();
        popUpList.setOnItemClickListener((position, message) -> {
            System.out.println(message);
        });

        FlatFrame frame = new FlatFrame();
        frame.setSize(500,500);
        frame.setLocationOnScreenCenter();
        JButton button = new JButton("클릭");
        button.addActionListener(e -> popUpList.show(MouseInfo.getPointerInfo().getLocation()));
        frame.getContainer().add(button);
        frame.show();
    }

    private JPanel getContentPane(){
        return FRAME.getCustomizablePanel();
    }

    private FlatLabel createLabel(String text){
        FlatLabel flatLabel = new FlatLabel(text);
        flatLabel.setBorder(BorderFactory.createEmptyBorder(PADDING/2,PADDING,PADDING/2,PADDING));
        return flatLabel;
    }

    private Dimension getProperSize(){
        int width = 0;
        int height = 0;

        for (Component comp : listView.getItems()){
            width = Math.max(comp.getPreferredSize().width, width);
            height += comp.getPreferredSize().height;
        }

        return new Dimension(width, height);
    }

    private Point getProperLocation(Dimension properSize, int originX, int originY){
        if (screenManager.getScreenWidth() < originX + properSize.width) {
            originX -= properSize.width;
        }

        if (screenManager.getWindowHeight() < originY + properSize.height){
            originY -= properSize.height;
        }

        return new Point(originX, originY);
    }

    public void addMenu(String menuTxt){
        listView.addItem(createLabel(menuTxt));
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void show(Point point){
        show(point.x, point.y);
    }

    public void show(int x, int y){
        Dimension properSize = getProperSize();
        listView.getComponent().setPreferredSize(properSize);
        FRAME.pack();
        FRAME.setLocation(getProperLocation(properSize, x, y));
        FRAME.setVisible(true);
    }

    public void setFocusLostDispose(){
        FRAME.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                dispose();
            }
        });
    }

    private void initListView(){
        listView.setTrace(true);
        listView.setSingleSelectionMode(true);
        listView.setOnSelectionListener(((beginIndex, endIndex, selectionList) -> {
            onItemClickListener.onItemClick(beginIndex, selectionList.get(0).getText());
            if (isDisposeAfterSelection) dispose();
        }));
    }

    public void dispose(){
        FRAME.dispose();
    }

    public void setDisposeAfterSelection(boolean isDisposeAfterSelection){
        this.isDisposeAfterSelection = isDisposeAfterSelection;
    }

    public void select(int index){
        listView.select(index, index);
    }

    public void deSelect(){
        listView.deSelect();
    }
}