# Convertir localidades INEGI a JSON

![portada](https://user-images.githubusercontent.com/4065733/46239898-25515300-c365-11e8-938f-f77c4e3986e2.png)


Convierte las localidades de INEGI (México) a formato JSON jerárquico. 

Sirve para usarse por ejemplo en este proyecto visualizador: 

[https://github.com/eduardoarandah/coordenadas-estados-municipios-localidades-de-mexico-json](https://github.com/eduardoarandah/coordenadas-estados-municipios-localidades-de-mexico-json) 

# Cómo usarse

Abra el proyecto en Netbeans y oprima F6

O Construya el proyecto y ejecútelo con 

	java -jar MakeMapData.jar

**El archivo de localidades se debe llamar "data.csv" y existir en la misma carpeta**

# Fuente

La información de este repositorio fue extraída de:

Catálogo Único de Claves de Áreas Geoestadísticas Estatales, Municipales y Localidades - consulta y descarga
[http://www.inegi.org.mx/geo/contenidos/geoestadistica/catalogoclaves.aspx](http://www.inegi.org.mx/geo/contenidos/geoestadistica/catalogoclaves.aspx) 

Usando estos parámetros: 

![parametros](https://user-images.githubusercontent.com/4065733/46239085-3bf1ad00-c359-11e8-961f-21ff442e1624.jpg)

# Objetivos

![https://user-images.githubusercontent.com/4065733/46239202-d0104400-c35a-11e8-9565-e8255cc37778.png](https://user-images.githubusercontent.com/4065733/46239202-d0104400-c35a-11e8-9565-e8255cc37778.png) 
- Generar mapa con los puntos geográficos latitud-longitud.
- Este mapa **NO debe requerir una base de datos**, para poder ser alojada fácilmente en un hosting gratuito como Netlify.
- Los marcadores geográficos serán "jerárquicos", cada punto puede tener "hijos" y estos hijos a su vez pueden tener "hijos", etc.
- Cuando vemos un punto, deberá mostrarnos su información y a la derecha, el mapa con sus hijos.
- Al hacer click en un punto en el mapa, mostrará información de ese punto (sin navegar a el).
- Al hacer DOBLE click en un punto, reemplazará la vista actual con la del punto señalado y mostrará un botón para regresar al "padre".
- La aplicación javascript funciona con cualquier tipo de puntos.
- Para el presente proyecto, mostrará todas las localidades del país con la jerarquía: Estado -> Municipio -> Localidad

# El Problema

El INEGI nos entrega un catálogo de localidades con coordenadas.

PERO no nos da la coordenada del municipio ni del estado. 

Entonces debemos calcular RECURSIVAMENTE:

- Coordenada del municipio a partir de las localidades
- Coordenadas del estado a partir de sus municipios

# Datos de ejemplo

![datos-ejemplo](https://user-images.githubusercontent.com/4065733/46240627-0e642e00-c370-11e8-8242-217011998f5c.png)


Este es un ejemplo de los datos, como podrá ver, solo tenemos la latitud y longitud decimal de la localidad. 
No contamos con coordenadas de estado ni municipio. 
Además, la cantidad de localidades es enorme, más de 300mil datos

# Lo que buscamos

Buscamos un esquema de “árbol” para poder ser representado por la herramienta visualizadora:
https://github.com/eduardoarandah/coordenadas-estados-municipios-localidades-de-mexico-json
He desarrollado esta herramienta para este proyecto, pero también para visualizar CUALQUIER información geográfica recursiva. 
Necesitamos un esquema así: 

![abc](https://user-images.githubusercontent.com/4065733/46240643-3e133600-c370-11e8-937f-c4a94c634692.png)

![def](https://user-images.githubusercontent.com/4065733/46240651-4b302500-c370-11e8-8573-8b56d26fd54f.png)

# Diseño de clases

![clase](https://user-images.githubusercontent.com/4065733/46240671-787cd300-c370-11e8-95cf-93b490d2fc93.png)

Para representar un punto geográfico generamos la clase “Punto” 
La cual puede contener recursivamente muchos puntos. 
Por favor note que “hijos” es un HashMap , cuyo índice es el nombre del punto. Esto es crucial porque nos permitirá buscar rápidamente y evitar duplicados. 
Cada punto puede tener varios hijos puntos, y a su vez estos tendrán hijos de forma recursiva sin límite. 

