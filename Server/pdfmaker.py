from fpdf import FPDF
def createPDF(fileName,permissionPrediction,recieversPrediction,servicesPrediction,appName,threshold,comboName):
    
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
    txt = '''Malware is a technical term and an abbreviated form of malicious software. The damage that malware does depends on the category of malware.
    In short, they create backdoors, corrupts files and spy on your online activities like passwords, credit card numbers, surfing habits and more. Not only has that but also infected entire network of devices and pops-up annoying ads.
    Android Malware could be mainly categorized as Adware, Spyware and Trojans which can affect the Android device in various ways as mentioned below.'''

    # pdf.set_font('Times', '', 12)
    # pdf.multi_cell(0, 5, txt)
    # pdf.ln()
    # pdf.set_font('Times', 'B', 13)
    # pdf.write(5, 'Adware: ')
    # pdf.set_font('Times', '', 12)
    # txt1 = "Not really malicious. But causes huge amount of inconvenience and can give way to a lot of other malware."
    # pdf.write(5, txt1)

    # pdf.ln(4)
    # pdf.ln(4)
    # pdf.set_font('Times', 'B', 13)
    # pdf.write(5, 'Spyware: ')
    # pdf.set_font('Times', '', 12)
    # txt2 = 'Malicious software that is designed to spy on user activities.'
    # pdf.write(5,txt2)

    # pdf.ln(4)
    # pdf.ln(4)
    # pdf.set_font('Times', 'B', 13)
    # pdf.write(5, 'Trojan: ')
    # pdf.set_font('Times', '', 12)
    # txt3 = 'Masks as a genuine software and creates backdoors to let malware in.'
    # pdf.write(5,txt3)
    # pdf.ln(4)
    # pdf.ln(4)

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
    pdf.write(5,appName)
    pdf.set_font('Times', '', 12)
    txt6 = ''' Application. We have applied both a Statistical as well as a Dynamic Approach to precisely analyze and judge whether the Application is malicious or benign and to what extent. 
    Our judgment based on permissions, API calls and Receiver services put the Application in one of the following categories: '''
    pdf.write(5,txt6)
    pdf.ln(4)
    pdf.ln(4)
    pdf.set_text_color(0,0,0)
    pdf.set_font('Times', 'B', 13)
    pdf.write(5,'Purely Benign: ')
    pdf.set_font('Times', '', 12)
    txt8 = "No Conflicting Permissions and Scripts Detected. This category states that the Application is totally safe and free from malware."
    pdf.write(5,txt8)
    pdf.ln(4)
    pdf.ln(4)
    pdf.set_text_color(0,0,0)
    pdf.set_font('Times', 'B', 13)
    pdf.write(5,'Low Level Risk: ')
    pdf.set_font('Times', '', 12)
    txt9 = "Few Permissions and Scripts may conflict but probably safe. A user may continue to use the Application but with caution. Any more permissions requested must be a red light"
    pdf.write(5,txt9)
    pdf.ln(4)
    pdf.ln(4)
    pdf.set_text_color(0,0,0)
    pdf.set_font('Times', 'B', 13)
    pdf.write(5,'Medium Level Risk: ')
    pdf.set_font('Times', '', 12)
    txt10 = "Some Permission and Scripts show malicious intends, the user should use the Application at their own risk but it is recommended to uninstall as per our analysis."
    pdf.write(5,txt10)
    pdf.ln(4)
    pdf.ln(4)
    pdf.set_text_color(0,0,0)
    pdf.set_font('Times', 'B', 13)
    pdf.write(5,'High Level Risk: ')
    pdf.set_font('Times', '', 12)
    txt10 = "Permissions and Scripts show pure malicious intends by the developer hence the user should uninstall the Application as soon as possible. "
    pdf.write(5,txt10)
    pdf.ln(4)
    pdf.ln(4)
    count = 0
    if permissionPrediction > threshold:
        count += 1
    if recieversPrediction > threshold:
        count += 1
    if servicesPrediction > threshold:
        count += 1
    print(count)

    pdf.set_font('Times', 'B', 13)
    pdf.write(5, 'Permissions Used: ')
    pdf.ln(4)
    with open('permissionToPDF.txt', 'rb') as fh:
        txt5 = fh.read().decode('latin-1')
    pdf.set_font('Times', 'I', 12)
    pdf.write(5,txt5)
    pdf.ln(4)
    pdf.ln(4)

    if count == 0 or count == 1:
        pdf.set_text_color(22,232,64)
        pdf.set_font('Times', 'B', 13)
        pdf.write(5,'Purely Benign: ')
        pdf.set_font('Times', '', 12)
        txt7 = "No Conflicting Permissions and Scripts Detected. This category states that the Application is totally safe and free from malware."
        pdf.write(5,txt7)
        pdf.ln(4)

    # elif count == 1:
    #     pdf.set_text_color(0,0,139)
    #     pdf.set_font('Times', 'B', 13)
    #     pdf.write(5,'Low Level Risk: ')
    #     pdf.set_font('Times', '', 12)
    #     txt7 = "Few Permissions and Scripts may conflict but it is probably safe to use. A user may continue to use this Application if any more system permissions are requested, uninstall recommended."
    #     pdf.write(5,txt7)

    elif count == 2:
        pdf.set_text_color(236,134,18)
        pdf.set_font('Times', 'B', 13)
        pdf.write(5,'Medium Level Risk: ')
        pdf.set_font('Times', '', 12)
        txt7 = "Some Permission and Scripts show malicious intends, the user should use the Application at their own risk but it is recommended to uninstall as per our analysis."
        pdf.write(5,txt7)

    elif count == 3:
        pdf.set_text_color(255,0,0)
        pdf.set_font('Times', 'B', 13)
        pdf.write(5,'High Level Risk: ')
        pdf.set_font('Times', '', 12)
        txt7 = "Permissions and Scripts show pure malicious intends by the developer hence the user should uninstall the Application as soon as possible. "
        pdf.write(5,txt7)

    pdf.output('/home/debian/Documents/res/Reports/' + comboName + '.pdf', 'F')