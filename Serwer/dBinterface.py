from sqlite3 import *
import pickle

data = pickle.load(open("data.pickle", "rb"))

class Unpickler:
    def loadObjectsTable(self):
        conn = connect('baza1.db')
        for i, e in enumerate(data):
            title = ""
            creator = ""
            description = ""
            form = ""
            date = ""
            for el in e:
                if "title" in el:
                    title = el[1]
                if "creator" in el:
                    creator = el[1]
                if "description" in el:
                    description = el[1]
                if "format" in el:
                    form = el[1]
                if "date" in el:
                    date = el[1]

            conn.execute("INSERT INTO Objects (id, title, creator, description, format, 'date') VALUES (?, ?, ?, ?, ?, ?)", (39000 + i, title, creator, description, form, date))
            conn.commit()
        conn.close()

    def loadSubjectsTable(self):
        conn = connect('baza1.db')
        for i, e in enumerate(data):
            for el in e:
                if ("subject" in el and "(" not in el[1] and "-" not in el[1] and "." not in el[1]): #type
                    temp = str(el[1])
                    conn.execute("INSERT INTO subjects (objectId, subject) VALUES (?, ?)", (39000 +i, temp))
                    conn.commit()

        conn.close()

    def loadTypesTable(self):
        conn = connect('baza1.db')
        for i, e in enumerate(data):
            for el in e:
                if ("type" in el and "(" not in el[1] and "-" not in el[1] and "." not in el[1]): #type
                    temp = str(el[1])
                    conn.execute("INSERT INTO types (objectId, type) VALUES (?, ?)", (39000 + i, temp))
                    conn.commit()
        conn.close()
