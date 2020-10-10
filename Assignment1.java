package guavaExamples;



import com.alibaba.fastjson.JSONObject;
import org.w3c.dom.ls.LSOutput;

import javax.servlet.ServletException;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

class MyEditorKit extends DefaultEditorKit
{
    public MyEditorKit(){
        super();
    }

    public ViewFactory getViewFactory(){
        return new MyViewFactory();
    }
}

class MyViewFactory implements ViewFactory
{
    public MyViewFactory(){
    }

    public View create(Element element){
        return new MyEditorView(element);
    }
}

class MyEditorView extends PlainView
{
    public MyEditorView(Element element){
        super(element);
    }

    protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1)
            throws BadLocationException{
        Document doc=getDocument();
        Segment segment=new Segment(), token=new Segment();
        int index=0, count=p1-p0;
        char c='\u0000';

        doc.getText(p0, count, segment);
        for(int i=0; i<count; i++){
            if(Character.isLetter(c=segment.array[segment.offset+i])){
                index=i;
                while(++i<count&&Character.isLetter(segment.array[segment.offset+i]));
                doc.getText(p0+index, (i--)-index, token);
                if(KeyWord.isKeyWord(token)){
                    g.setFont(KEYWORDFONT);
                    g.setColor(KEYWORDCOLOR);
                }else{
                    g.setFont(TEXTFONT);
                    g.setColor(TEXTCOLOR);
                }
                x=Utilities.drawTabbedText(token, x, y, g, this, p0+index);
                continue;
            }else if(c=='/'){
                index=i;
                if(++i<count&&segment.array[segment.offset+i]=='/'){
                    doc.getText(p0+index, count-index, token);
                    g.setFont(COMMENTFONT);
                    g.setColor(COMMENTCOLOR);
                    x=Utilities.drawTabbedText(token, x, y, g, this, p0+index);
                    break;
                }
                doc.getText(p0+index, 1, token);
                g.setFont(TEXTFONT);
                g.setColor(TEXTCOLOR);
                x=Utilities.drawTabbedText(token, x, y, g, this, p0+index);
                i--;
                continue;
            }else if(c=='\''||c=='\"'){
                index=i;
                char ch='\u0000';
                while(++i<count){
                    if((ch=segment.array[segment.offset+i])=='\\'){
                        i++;
                        continue;
                    }else if(ch==c) break;
                }
                if(i>=count) i=count-1;
                doc.getText(p0+index, i-index+1, token);
                g.setFont(STRINGFONT);
                g.setColor(STRINGCOLOR);
                x=Utilities.drawTabbedText(token, x, y, g, this, p0+index);
                continue;
            }else{
                index=i;
                while(++i<count&&!Character.isLetter((c=segment.array[segment.offset+i]))&&c!='/'&&c!='\''&&c!='\"');
                doc.getText(p0+index, (i--)-index, token);
                g.setFont(TEXTFONT);
                g.setColor(TEXTCOLOR);
                x=Utilities.drawTabbedText(token, x, y, g, this, p0+index);
            }
        }
        return x;
    }

    protected int drawSelectedText(Graphics g, int x, int y, int p0, int p1)
            throws BadLocationException{
        g.setFont(TEXTFONT);
        g.setColor(TEXTCOLOR);
        return super.drawSelectedText(g, x, y, p0, p1);
    }

    public static Font TEXTFONT=new Font("DialogInput", Font.PLAIN, 11);
    public static Color TEXTCOLOR=Color.black;
    public static Font KEYWORDFONT=new Font(TEXTFONT.getFontName(), Font.BOLD, TEXTFONT.getSize());
    public static Color KEYWORDCOLOR=new Color(0, 0, 128);
    public static Font  COMMENTFONT=TEXTFONT;
    public static Color COMMENTCOLOR=new Color(187, 29, 160);
    public static Font STRINGFONT=TEXTFONT;
    public static Color STRINGCOLOR=new Color(38, 255, 0);
}
class KeyWord
{
    public KeyWord(){
    }

    public static boolean isKeyWord(Segment seg){
        boolean isKey=false;
        for(int i=0; !isKey&&i<KEYWORDS.length; i++)
            if(seg.count==KEYWORDS[i].length()){
                isKey=true;
                for(int j=0; isKey&&j<seg.count; j++)
                    if(seg.array[seg.offset+j]!=KEYWORDS[i].charAt(j))
                        isKey=false;

            }
        return isKey;
    }

    public static final String[] KEYWORDS={
            "abstract",
            "boolean", "break", "byte",
            "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double",
            "else", "extends",
            "final", "finally", "float", "for",
            "goto",
            "if", "implements", "import", "instanceof", "int", "interface",
            "long",
            "native", "new",
            "package",
            "private", "protected", "public",
            "return",
            "short", "static", "strictfp", "super", "switch", "synchronized",
            "this", "throw", "throws", "transient", "try",
            "void", "volatile",
            "while",
            "true", "false"
    };
}


public class Assignment1 extends JFrame implements DocumentListener {

    private static final long serialVersionUID = 1L;
    JTextField content=new JTextField();
    JMenuBar menubar=new JMenuBar();
    JMenu file=new JMenu("File");
    JMenu edit=new JMenu("edit");
    JMenu view=new JMenu("View");
    JMenu help =new JMenu("Help");
    MyEditorKit  kit=new MyEditorKit();
    JEditorPane wordarea=new JEditorPane();
    JScrollPane imgScrollPane = new JScrollPane(wordarea);
    String [] str1={"New","Open","Save","Print","Exit"};
    String [] str2={"Cut","Copy","Paste","Search","Cut(SCPC)","T&D"};
    String [] str3={"About"};
    String name1=JsonServerlst.service();
    String name2=JsonServerlst.NAME1();
    String num1=JsonServerlst.number1();
    String num2=JsonServerlst.number2();





    int flag=0;
    String source="";
    public static void main(String[] args) throws ServletException, IOException {
        JFrame Assignmet1=new Assignment1();
        Assignmet1.setVisible(true);

    }
    public Assignment1() throws ServletException, IOException {
        setTitle("Test Editor");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        setSize(screenSize.width/2,screenSize.height/2);
        setLocation(screenSize.width/4,screenSize.height/4);
        add(imgScrollPane,BorderLayout.CENTER);
        setJMenuBar(menubar);
        menubar.add(file);
        menubar.add(view);
        menubar.add(help);
        menubar.add(edit);
        wordarea.getDocument().addDocumentListener(this);
        for(int i=0;i<str1.length;i++){
            JMenuItem item1= new JMenuItem(str1[i]);
            item1.addActionListener(new MyActionListener1());
            file.add(item1);
        }
        for(int i=0;i<str2.length;i++){
            JMenuItem item2= new JMenuItem(str2[i]);
            item2.addActionListener(new MyActionListener1());
            edit.add(item2);
        }
        for(int i=0;i<str3.length;i++){
            JMenuItem item3= new JMenuItem(str3[i]);
            item3.addActionListener(new MyActionListener1());
            help.add(item3);
        }
        initFrame();
    }
    private void initFrame(){
        wordarea.setFont(new Font("DialogInput", Font.PLAIN, 11));
        wordarea.setEditorKitForContentType("text/java", kit);
        wordarea.setContentType("text/java");

        wordarea.addCaretListener(new CaretListener(){
            public void caretUpdate(CaretEvent e){
            }
        });
    }
    public void changedUpdate(DocumentEvent e) {
        flag=1;
    }

    public void insertUpdate(DocumentEvent e) {
        flag=1;
    }
    public void removeUpdate(DocumentEvent e) {
        flag=1;
    }
    public void display(DocumentEvent e) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    void open(){
        FileDialog  filedialog=new FileDialog(this,"打开",FileDialog.LOAD);
        filedialog.setVisible(true);
        String path=filedialog.getDirectory();
        String name=filedialog.getFile();
        if(path!=null && name!=null){
            FileInputStream file = null;
            try {
                file = new FileInputStream(path+name);
            } catch (FileNotFoundException e) {

            }
            InputStreamReader put =new InputStreamReader(file);
            BufferedReader in=new BufferedReader(put);
            String temp="";
            String now = null;
            try {
                now = (String)in.readLine();
            } catch (IOException e) {

                e.printStackTrace();
            }
            while(now!=null){
                temp+=now+"\r\n";
                try {
                    now=(String)in.readLine();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

            wordarea.setText(temp);
        }

    }

    void save(){
        FileDialog  filedialog=new FileDialog(this,"Save",FileDialog.SAVE);
        filedialog.setVisible(true);
        if(filedialog.getDirectory()!=null && filedialog.getFile()!=null){
            OutputStreamWriter out = null;
            //                out = new OutputStreamWriter(new FileOutputStream(filedialog.getDirectory()+filedialog.getFile()));
            try {
                out.write(wordarea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
            flag=0;
            try {
                out.close();
                source=wordarea.getText();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void newfile(){
        if(flag==0){
            wordarea.setText("");
        }
        if(flag==1){
            int m=  JOptionPane.showConfirmDialog(this,"Should you want to save the file");
            if(m==0){
                save();
                wordarea.setText("");
            }

            if(m==1){
                wordarea.setText("");
                flag=0;
            }
        }
    }
    void time(){

    }
    void exit(){
        if(flag==0){
            System.exit(0);
        }
        if(flag==1){
            int m=  JOptionPane.showConfirmDialog(this,"Should you want to save the file");
            if(m==0){
                save();
            }
            if(m==1){
                System.exit(0);
            }
        }
    }



    void about(){
        JOptionPane.showMessageDialog(null,"The Test Editor is made by" +name1+" and"+ name2);
    }
    class MyActionListener1 implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()instanceof JMenuItem){
                if(e.getActionCommand()=="Cut"){
                    wordarea.cut();
                }
                if(e.getActionCommand()=="Copy"){
                    wordarea.copy();
                }
                if(e.getActionCommand()=="Paste"){
                    wordarea.paste();
                }
                if(e.getActionCommand()=="Search"){
                    JFrame frame = new JFrame("Search diagram");
                    frame.setVisible(true);
                    frame.setLocation(270, 200);
                    frame.setSize(300,100);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    JLabel label = new JLabel("Search content");
                    content.setBounds(90, 20, 180, 30);
                    frame.add(content);
                    frame.add(label);

                    content.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String keyword=content.getText();
                            String textInText = wordarea.getText();
                            int index = textInText.indexOf(keyword);
                            if (index != -1) {
                                wordarea.getHighlighter().removeAllHighlights();
                                try {
                                    ArrayList<ArrayList<Integer>> indexList= new ArrayList<ArrayList<Integer>>();
                                    indexList=Search.search(textInText,keyword);
                                    int i=0;
                                    while (i<indexList.size()) {
                                        wordarea.getHighlighter().addHighlight(indexList.get(i).get(0), indexList.get(i).get(1),DefaultHighlighter.DefaultPainter);
                                        i+=1;
                                    }
                                } catch (BadLocationException e1) {

                                    e1.printStackTrace();
                                }
                            }
                            else {
                                JOptionPane.showMessageDialog(null,"Can't find keyword:" + keyword);
                            }
                        }
                    });

                }
                if(e.getActionCommand()=="Open"){
                    open();
                }
                if(e.getActionCommand()=="Save"){
                    save();
                }
                if(e.getActionCommand()=="New"){
                    newfile();
                }
                if(e.getActionCommand()=="Exit"){
                    exit();
                }
                if(e.getActionCommand()=="T&D"){
                    time();
                }
                if(e.getActionCommand()=="About"){
                    about();
                }
            }

        }

    }
}