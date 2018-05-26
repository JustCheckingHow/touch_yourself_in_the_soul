import pickle
import operator

x = pickle.load(open("data.pickle", "rb"))

hist = dict()
for i in range(3000):
    for el in x[i]:
        if ("subject" in el and "(" not in el[1] and "-" not in el[1] and "." not in el[1]): #type
            temp = str(el[1])
            try:
                hist[temp] += 1
            except:
                hist[temp] = 1

sorted_x = sorted(hist.items(), key=operator.itemgetter(1))
f = open("subjects", "w")
subject = ""
for el in sorted_x:
    subject += el[0]

# print(sorted_x)


#subject:
# portrety
# architektura
#
