import flask
from flask import send_from_directory,send_file
import werkzeug
import time
import mysql.connector

app = flask.Flask(__name__)
@app.route('/', methods = ['GET', 'POST'])
def handle_request():
    files_ids = list(flask.request.files)
    print(files_ids)
    print("\nNumber of Received APK's : ", len(files_ids))
    for file_id in files_ids:
        apkfile = flask.request.files[file_id]
        filename = werkzeug.utils.secure_filename(apkfile.filename)
        print(filename)
        print(files_ids) 
        print(filename)
        temp = list(filename.split('696969'))
        print(temp)
        appname = temp[0]
        flag = temp[2]
        print(flag)
        #print(filename,appname,token)
        ip = flask.request.remote_addr
        mydb = mysql.connector.connect(
        host="localhost",
        user="root",
        password="toor",
        database="ANDMAL"
        )
        apkfile.save('TokenName.txt')
        with open('TokenName.txt', 'r') as tk:
            token = tk.readline().lstrip().rstrip()
        print(token)
        mycursor = mydb.cursor()
        sql = "INSERT INTO FILESTORE (filename,ip_addr,token,appname,flag) VALUES (%s,%s,%s,%s,%s)"
        mycursor.execute(sql,(appname,ip,token,files_ids[0],flag))
        mydb.commit()
        if str(flag) == '1':
            sql2 = "INSERT INTO apilist (filename, appname, token) VALUES (%s,%s,%s)"
            mycursor.execute(sql2,(appname,files_ids[0],token))
            mydb.commit()
        print(mycursor.rowcount, "record inserted.")
        #timestr = time.strftime("%Y%m%d-%H%M%S")
        
    print("\n")
    #print('test')
    #downloadFile()
    return send_file('/home/debian/Documents/sample.pdf',as_attachment=True)
     
app.run(host="0.0.0.0", port=80, debug=True)