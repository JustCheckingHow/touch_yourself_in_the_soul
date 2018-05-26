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
            print(f'\r {i+1}/{len(self.data)} ', end='')

        conn.close()

    def loadSubjectsTable(self):
        conn = connect(_DATABASE_)
        for i, e in enumerate(self.data):
            for el in e:
                if ("subject" in el and "(" not in el[1] and "-" not in el[1] and "." not in el[1]): #type
                    temp = str(el[1])
                    conn.execute("INSERT INTO subjects (objectId, subject) VALUES (?, ?)", (39000 +i, temp))
                    conn.commit()
            print(f'\r{i+1}/{len(self.data)} ', end='')

        conn.close()

    def loadTypesTable(self):
        conn = connect(_DATABASE_)
        for i, e in enumerate(self.data):
            for el in e:
                if ("type" in el and "(" not in el[1] and "-" not in el[1] and "." not in el[1]): #type
                    temp = str(el[1])
                    conn.execute("INSERT INTO types (objectId, type) VALUES (?, ?)", (39000 + i, temp))
                    conn.commit()
            print(f'\r{i+1}/{len(self.data)} ', end='')

        conn.close()

    def clearOrphanedExhibits(self):
        conn = connect(_DATABASE_)
        conn.execute("delete from Objects where id not in (select distinct objectId from subjects) or id not in (select distinct objectId from types)")
        conn.commit()

    def limitTypesTo(self, num):
        qry = "delete from types where id not in (select id from types group by type order by count(objectId) desc limit %i)" % num
        conn = connect(_DATABASE_)
        conn.execute(qry)
        conn.commit()

    def limitSubjectsTo(self, num):
        qry = "delete from subjects where id not in (select subject, count(objectId) from subjects group by subject order by count(objectId) desc limit %i)" % num
        conn = connect(_DATABASE_)
        conn.execute(qry)
        conn.commit()

    def purgeDataBase(self):
        conn = connect(_DATABASE_)
        conn.execute("delete from Objects where 1=1")
        conn.execute("delete from subjects where 1=1")
        conn.execute("delete from types where 1=1")
        conn.commit()

u = Unpickler()
u.purgeDataBase()
print('Purged database')

u.loadObjectsTable()
print('Loaded objects table')

u.loadTypesTable()
print('Loaded types table')

u.loadSubjectsTable()
print('Loaded subjects table')

u.limitTypesTo(20)
u.limitSubjectsTo(20)
print('Limited categories')

u.clearOrphanedExhibits()
print('Cleaned up DB')
