import networkx as nx
from collections import defaultdict
import time
import os
from androguard.misc import AnalyzeAPK
import argparse
import glob
from multiprocessing import Pool as ThreadPool
from functools import partial
import zipfile
from API_prediction import Predictor
import mysql.connector
from google.cloud import storage
from API_PDFMaker import createPDF



def get_call_graph(dx):
    t0 = time.time()
    CG = nx.DiGraph()
    nodes = dx.find_methods('.*', '.*', '.*', '.*')
    for m in nodes:
        API = m.get_method()
        class_name = API.get_class_name()
        method_name = API.get_name()
        descriptor = API.get_descriptor()
        api_call = class_name + '->' + method_name + descriptor

        if len(m.get_xref_to()) == 0:
            continue
        CG.add_node(api_call)

        for other_class, callee, offset in m.get_xref_to():
            _callee = callee.get_class_name() + '->' + callee.get_name() + callee.get_descriptor()
            CG.add_node(_callee)
            if not CG.has_edge(API, callee):
                CG.add_edge(api_call, _callee)

    return CG

def apk_to_callgraph(app_path, exist_files, out_path):
    apk_name = app_path.split('/')[-1].split('.apk')[0]

    a, d, dx = AnalyzeAPK(app_path)
    call_graph = get_call_graph(dx=dx)
    cg = call_graph
    print(apk_name)
    print("Writing...")
    file_cg = out_path + '/' + apk_name + '.gexf'
    print(out_path, apk_name)
    nx.write_gexf(cg, file_cg)
    print("Writing Done")

def main(filename):
    tic = time.time()
    client = storage.Client()
    with open('/home/debian/Documents/run_server/API_processing/testapk.apk','wb') as f:
        client.download_blob_to_file('gs://andmaldeploy-testphase1/apks/'+ filename + '.apk',f)

    output = "/home/debian/Documents/run_server/API_processing/"
    i = '/home/debian/Documents/run_server/API_processing/testapk.apk'
    try:
        print(i)
        file = i
        exist_files = os.listdir(output)
        exist_files = [f.split('.gexf')[0] for f in exist_files]
        if output[-1] == '/':
            out_path = output[:-1]
        else:
            out_path = output

        if os.path.isdir(file):
            if file[-1] == '/':
                path = file + '*.apk'
            else:
                path = file + '/*.apk'

            apks = glob.glob(path)
            pool = ThreadPool(15)
            pool.map(partial(apk_to_callgraph, exist_files=exist_files, out_path=out_path), apks)
        else:
            apk_to_callgraph(file, exist_files, out_path)
        count += 1
        print(count)
    except:
        pass


import networkx as nx
import time
import argparse
import csv
from multiprocessing import Pool as ThreadPool
from functools import partial
import glob

def obtain_sensitive_apis(file):
    print("In Obtain_Sensitive_APIs")
    sensitive_apis = []
    with open(file, 'r') as f:
        for line in f.readlines():
            if line.strip() == '':
                continue
            else:
                sensitive_apis.append(line.strip())
    print("Out Obtain_Sensitive_APIs")
    return sensitive_apis

def callgraph_extraction(file):
    CG = nx.read_gexf(file)
    return CG

def degree_centrality_feature(file, sensitive_apis):
    print("In Degree")
    sha256 = file.split('/')[-1].split('.')[0]
    CG = callgraph_extraction(file)
    node_centrality = nx.degree_centrality(CG)
        
    vector = []
    for api in sensitive_apis:
        if api in node_centrality.keys():
            vector.append(node_centrality[api])
        else:
            vector.append(0)
    print("out Degree")
    return (sha256, vector)

def katz_centrality_feature(file, sensitive_apis):
    print("In Katz")
    sha256 = file.split('/')[-1].split('.')[0]
    CG = callgraph_extraction(file)
    node_centrality = nx.katz_centrality(CG)

    vector = []
    for api in sensitive_apis:
        if api in node_centrality.keys():
            vector.append(node_centrality[api])
        else:
            vector.append(0)
    print("Out Katz")
    return (sha256, vector)

def closeness_centrality_feature(file, sensitive_apis):
    print("In Closeness")
    sha256 = file.split('/')[-1].split('.')[0]
    CG = callgraph_extraction(file)
    node_centrality = nx.closeness_centrality(CG)
    
    vector = []
    for api in sensitive_apis:
        if api in node_centrality.keys():
            vector.append(node_centrality[api])
        else:
            vector.append(0)
    print("Out Closeness")
    return (sha256, vector)

def harmonic_centrality_feature(file, sensitive_apis):
    print("In Harmonic")
    sha256 = file.split('/')[-1].split('.')[0]
    CG = callgraph_extraction(file)
    node_centrality = nx.harmonic_centrality(CG)
    
    vector = []
    for api in sensitive_apis:
        if api in node_centrality.keys():
            vector.append(node_centrality[api])
        else:
            vector.append(0)
    print("Out Harmonic")
    return (sha256, vector)

def obtain_dataset(dataset_path, centrality_type, sensitive_apis):
    Vectors = []
    Labels = []
    
    print("In Obtain_Dataset")
    if dataset_path[-1] == '/':
        apps_t = glob.glob(dataset_path + 'testapk.gexf')
    else:
        apps_t = glob.glob(dataset_path + 'testapk.gexf')
    print(len(apps_t))

    pool_t = ThreadPool(15)
    if centrality_type == 'degree':
        vector_t = pool_t.map(partial(degree_centrality_feature, sensitive_apis=sensitive_apis), apps_t)
    elif centrality_type == 'katz':
        vector_t = pool_t.map(partial(katz_centrality_feature, sensitive_apis=sensitive_apis), apps_t)
    elif centrality_type == 'closeness':
        vector_t = pool_t.map(partial(closeness_centrality_feature, sensitive_apis=sensitive_apis), apps_t)
    # elif centrality_type == 'harmonic':
    #     vector_t = pool_t.map(partial(harmonic_centrality_feature, sensitive_apis=sensitive_apis), apps_t)
    else:
        print('Error Centrality Type!')

    Vectors.extend(vector_t)
    Labels.extend([0 for i in range(len(vector_t))])

    print("Out Obtain_Dataset")
    return Vectors, Labels

def main2(filename,appname,token):
    sensitive_apis_path = '/home/debian/Documents/res/sensitive_apis.txt'
    sensitive_apis = obtain_sensitive_apis(sensitive_apis_path)

    #args = parseargs()
    dataset_path = "/home/debian/Documents/run_server/API_processing/"
    output_path = "/home/debian/Documents/run_server/API_processing/CSVs"

    for cetrality_type in ['degree', 'katz', 'closeness', 'harmonic']:
        Vectors, Labels = obtain_dataset(dataset_path, cetrality_type, sensitive_apis)
        feature_csv = [[] for i in range(len(Labels)+1)]
        feature_csv[0].append('SHA256')
        feature_csv[0].extend(sensitive_apis)
        feature_csv[0].append('Label')

        for i in range(len(Labels)):
            (sha256, vector) = Vectors[i]
            feature_csv[i+1].append(sha256)
            feature_csv[i+1].extend(vector)
            feature_csv[i+1].append(Labels[i])

        if output_path[-1] == '/':
            csv_path = output_path + cetrality_type + '_features.csv'
        else:
            csv_path = output_path + '/' + cetrality_type + '_features.csv'

        with open(csv_path, 'w', newline='') as f:
            csvfile = csv.writer(f)
            csvfile.writerows(feature_csv)
    comboName = appname + '@' + filename + 'API_'
    print(appname)
    Predictor(filename,appname,token,comboName)

def call(filename,appname,token):
    main(filename)
    main2(filename,appname,token)
sqlDelete = "DELETE FROM apilist WHERE filename = %s"       
while True:
  mydb = mysql.connector.connect(
        host="localhost",
        user="root",
        password="toor",
        database="ANDMAL"
        )
  mycursor = mydb.cursor()
  mycursor.execute("SELECT * FROM apilist")
  rs = mycursor.fetchall()
  row_count = mycursor.rowcount
  if row_count > 0:
    for i in range(row_count):
      call(rs[i][0],rs[i][1],rs[i][2])
      mycursor.execute(sqlDelete, (rs[i][0],))
      mydb.commit()
    # print(time.time()-tic)