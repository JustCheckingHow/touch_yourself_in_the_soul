from sqlite3 import *
import pickle



_DATABASE_ = "baza1.db"



class DBInterface:

    def __init__(self):
        self.conn = connect(_DATABASE_)

    def getSubject(self, id):
        cur = self.conn.execute("SELECT subject FROM subjects WHERE objectId = "+str(id)+"")
        return cur.fetchall()

    def getObject(self, id):
        cur = self.conn.execute("SELECT title, creator, description, format, date, identifier FROM Objects WHERE id = "+str(id)+"")
        return cur.fetchall()

    def getType(self, id):
        cur = self.conn.execute("SELECT type FROM types WHERE objectId = "+str(id)+"")
        return cur.fetchall()

class Unpickler:
    def __init__(self):
        self.data = pickle.load(open("data.pickle", "rb"))
        with open("subjects.txt", "r") as f:
            self.filters = f.readlines()
        self.filters = [x.strip() for x in self.filters]
        print(self.filters)

    def loadObjectsTable(self):
        conn = connect(_DATABASE_)
        for i, e in enumerate(self.data):
            title = ""
            creator = ""
            description = ""
            form = ""
            date = ""
            identifier = ""
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
                if "identifier" in el:
                    if el[1].find("."):
                        el[1]+="(p)"
                    identifier = el[1].lower().replace(".", "").replace("/", "_").replace(" ", "")

            conn.execute("INSERT INTO Objects (id, title, creator, description, format, 'date', identifier) VALUES (?, ?, ?, ?, ?, ?, ?)", (39000 + i, title, creator, description, form, date, identifier))
            conn.commit()
        conn.close()

    def loadSubjectsTable(self):
        conn = connect(_DATABASE_)
        for i, e in enumerate(self.data):
            for el in e:
                if ("subject" in el and "(" not in el[1] and "-" not in el[1] and "." not in el[1]): #type
                    temp = str(el[1])
                    if temp in self.filters:
                        conn.execute("INSERT INTO subjects (objectId, subject) VALUES (?, ?)", (39000 +i, temp))
                        conn.commit()

        conn.close()

    def loadTypesTable(self):
        conn = connect(_DATABASE_)
        for i, e in enumerate(self.data):
            for el in e:
                if ("type" in el and "(" not in el[1] and "-" not in el[1] and "." not in el[1]): #type
                    temp = str(el[1])
                    conn.execute("INSERT INTO types (objectId, type) VALUES (?, ?)", (39000 + i, temp))
                    conn.commit()
        conn.close()

# u = Unpickler()
# u.loadObjectsTable()
# u.loadTypesTable()
# u.loadSubjectsTable()
