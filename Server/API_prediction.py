def Predictor(filename,appname,token,comboName):
    import keras
    import pandas as pd
    import numpy as np
    from sklearn.preprocessing import Normalizer
    from APINotifier import NotificationGenerator
    from API_PDFMaker import createPDF
    from API_cloudUpload import upload_blob
    def Degree():
        print("Degree")
        model = keras.models.load_model("/home/debian/Documents/res/APIs/LSTM_degree_model.hdf5")
        predictiondata = pd.read_csv('/home/debian/Documents/run_server/API_processing/CSVs/degree_features.csv')
        X = predictiondata.iloc[:,1:(predictiondata.shape[1]-1)]
        scaler = Normalizer().fit(X)
        predX = scaler.transform(X)
        np.set_printoptions(precision=3)
        X_pred = np.reshape(predX, (predX.shape[0], predX.shape[0], predX.shape[1]))
        y_pred = model.predict(X_pred)
        return(y_pred)

    def Closeness():
        print("Closeness")
        model = keras.models.load_model("/home/debian/Documents/res/APIs/LSTM_closeness_model.hdf5")
        predictiondata = pd.read_csv('/home/debian/Documents/run_server/API_processing/CSVs/closeness_features.csv')
        X = predictiondata.iloc[:,1:(predictiondata.shape[1]-1)]
        scaler = Normalizer().fit(X)
        predX = scaler.transform(X)
        np.set_printoptions(precision=3)
        X_pred = np.reshape(predX, (predX.shape[0], predX.shape[0], predX.shape[1]))
        y_pred = model.predict(X_pred)
        return(y_pred)

    def Harmonic():
        print("Harmonic")
        model = keras.models.load_model("/home/debian/Documents/res/APIs/LSTM_Harmonic.hdf5")
        predictiondata = pd.read_csv('/home/debian/Documents/run_server/API_processing/CSVs/harmonic_features.csv')
        X = predictiondata.iloc[:,1:(predictiondata.shape[1]-1)]
        scaler = Normalizer().fit(X)
        predX = scaler.transform(X)
        np.set_printoptions(precision=3)
        X_pred = np.reshape(predX, (predX.shape[0], predX.shape[0], predX.shape[1]))
        y_pred = model.predict(X_pred)
        return(y_pred)

    def Katz():
        print("Katz")
        model = keras.models.load_model("/home/debian/Documents/res/APIs/LSTM_katz_model.hdf5")
        predictiondata = pd.read_csv('/home/debian/Documents/run_server/API_processing/CSVs/katz_features.csv')
        X = predictiondata.iloc[:,1:(predictiondata.shape[1]-1)]
        scaler = Normalizer().fit(X)
        predX = scaler.transform(X)
        np.set_printoptions(precision=3)
        X_pred = np.reshape(predX, (predX.shape[0], predX.shape[0], predX.shape[1]))
        y_pred = model.predict(X_pred)
        return(y_pred)

    katzpred = Katz()
    closenesspred = Closeness()
    degreepred = Degree()
    createPDF(filename,appname,token,katzpred,degreepred,closenesspred,comboName)
    upload_blob(filename,appname,comboName)
    NotificationGenerator(token, appname,filename,comboName)

