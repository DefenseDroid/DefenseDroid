from fpdf import FPDF
def createPDF(filename,appname,tokenfileName,katzpred,degreepred,closenesspred,comboName):
    
    title = 'DefenseDroid Execution Report'
    pdf = FPDF()
    pdf.add_page()
    pdf.image('/home/debian/Documents/res/app_icon.png', x=8, y=4, w=50, h=25)
    pdf.set_font('Arial', 'B', 15)
    w = pdf.get_string_width(title) + 6
    pdf.set_x((210 - w) / 2)
    pdf.set_draw_color(0, 80, 180)
    pdf.set_fill_color(230, 230, 0)
    pdf.set_text_color(0, 0, 0)
    pdf.set_line_width(1)
    pdf.cell(w, 9, title, 1, 1, 'C', 1)
    pdf.ln(10)
    pdf.set_font('Arial', 'B', 12)
    pdf.set_fill_color(200, 220, 255)
    pdf.cell(0, 6, '%d : %s' % (1, 'About Malware'), 0, 1, 'L', 1)
    pdf.ln(4)
    txt = '''Malware is a technical term and an abbreviated form of malicious software. The damage that malware does depends on the category of malware.In short, they create backdoors, corrupts files and spy on your online activities like passwords, credit card numbers, surfing habits and more. Not only has that but also infected entire network of devices and pops-up annoying ads.Android Malware could be mainly categorized as Adware, Spyware and Trojans which can affect the Android device in various ways as mentioned below.'''

    txt4 = '''Most Android malware do not attempt to perform exploits to get to root, as that is not required for nefarious motives. 
                Rather, apps are commonly modified to add in a hidden Trojan component so that when a user installs an app the Trojan is also installed. 
                Once installed and run, Android malware may employ a wide variety of permissions enabled for the app to then send text messages, 
                and phone and geo-location information to manage and intercept all types of communications and more.'''
    pdf.write(5,txt4)
    pdf.ln(4)
    pdf.ln(4)

    pdf.set_font('Times', '', 12)
    pdf.write(5,'This Report consists of the Analysis of ')
    pdf.set_font('Times', 'B', 13)
    pdf.write(5,appname)
    pdf.set_font('Times', '', 12)
    txt6 = ''' Application. We have applied a Dynamic Approach to precisely analyze and judge whether the Application is malicious or benign and to what extent. Our judgment, which is based on three factors Katz, Closeness and Degree. '''
    pdf.write(5,txt6)
    pdf.ln(4)
    pdf.ln(4)

    
    pdf.set_font('Times', '', 12)
    txt6 = '''In graph theory, the Katz centrality of a node is a measure of centrality in a network. It is used to measure the relative degree of influence of an actor (or node) within a social network. Unlike typical centrality measures which consider only the shortest path (the geodesic) between a pair of actors, Katz centrality measures influence by taking into account the total number of walks between a pair of actors. It is similar to Google's PageRank and to the eigen vector centrality.'''
    pdf.write(5,txt6)
    pdf.ln(4)
    pdf.ln(4)

    txt6 = ''' In a connected graph, closeness centrality of a node is a measure of centrality in a network, calculated as the reciprocal of the sum of the length of the shortest paths between the node and all other nodes in the graph. Thus, the more central a node is, the closer it is to all other nodes. '''
    pdf.write(5,txt6)
    pdf.ln(4)
    pdf.ln(4)

    txt6 = ''' Degree centrality is defined as the number of links incident upon a node (i.e., the number of ties that a node has). If the network is directed (meaning that ties have direction), then two separate measures of degree centrality are defined, namely, in-degree and out-degree. '''
    pdf.write(5,txt6)
    pdf.ln(4)
    pdf.ln(4)

    pdf.set_font('Times', 'B', 12)
    txt6 = ''' Depending on the results we put the Application in one of the two categories: '''
    pdf.write(5,txt6)
    pdf.ln(4)
    pdf.ln(4)

    pdf.set_font('Times', '', 12)
    txt6 =  'Our deep analysis model has predicted that the application ' + appname +  ' is'
    pdf.write(5,txt6)
    pdf.ln(4)
    pdf.ln(4)

    count = 0
    if katzpred >= 0.5:
        count += 1
    if degreepred >= 0.5:
        count += 1
    if closenesspred >= 0.5:
        count  += 1
    
    if count >= 2:
        pdf.set_text_color(255,0,0)
        pdf.set_font('Times', 'B', 13)
        pdf.write(5,'Malicious: ')
        pdf.set_font('Times', '', 12)
        txt7 = '''Permissions and Scripts show pure malicious intents by the developer hence the user should uninstall the Application as soon as possible. No exceptions can be tolerated. '''
        pdf.write(5,txt7)
    
    else:
        pdf.set_text_color(22,232,64)
        pdf.set_font('Times', 'B', 13)
        pdf.write(5,'Benign: ')
        pdf.set_font('Times', '', 12)
        txt7 = '''Conflicting Permissions and Scripts Detected. This category states that the Application is totally safe and free from malware.'''
        pdf.write(5,txt7)
        pdf.ln(4)
    print('\n',comboName)
    pdf.output('/home/debian/Documents/run_server/API_processing/Reports/' + comboName + '.pdf', 'F')









