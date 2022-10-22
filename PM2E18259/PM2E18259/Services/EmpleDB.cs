using SQLite;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace PM2E18259.Services
{
    public class EmpleDB
    {
        readonly SQLiteAsyncConnection db;

        public EmpleDB()
        {
        }
        public EmpleDB(String pathbasedatos)
        {
            db = new SQLiteAsyncConnection(pathbasedatos);
            db.CreateTableAsync<Lugar>();
        }

        public Task<List<Lugar>> listaempleados()
        {
            return db.Table<Lugar>().ToListAsync();
        }

        public Task<Lugar> ObtenerEmpleado(Int32 pcodigo)
        {
            return db.Table<Lugar>().Where(i => i.id == pcodigo).FirstOrDefaultAsync();
        }

        public Task<Int32> EmpleadoGuardar(Lugar emple)
        {
            if (emple.id != 0)
            {
                return db.UpdateAsync(emple);
            }
            else
            {
                return db.InsertAsync(emple);
            }
        }

        public Task<Int32> EmpleadoBorrar(Lugar emple)
        {
            return db.DeleteAsync(emple);
        }
    }
}
