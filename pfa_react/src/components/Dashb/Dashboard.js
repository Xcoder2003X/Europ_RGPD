import React, { useState, useEffect } from "react";
import axios from "axios";
import { 
 Bar,  Pie, Cell, Tooltip, 
  XAxis, YAxis, CartesianGrid, Legend, ResponsiveContainer
} from "recharts";
import { lazy, Suspense } from 'react';
import { LoadingSpinner } from "../LoadingSpinner";
import { ErrorMessage } from "../ErrorMessage";


const COLORS = ["#82ca9d", "#8884d8", "#ff7300", "#ffbb28", "#ff6384"];

const Dashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);

  useEffect(() => {
    fetchData();
  }, []);

  const handlePageChange = (newPage) => {
    setPage(newPage);
    fetchData(newPage);
    console.log(newPage + "was clicked");
  };

  const fetchData = async (pageNumber = page) => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`http://localhost/api/reports/dashboard?page=${pageNumber}&size=8`,{
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      setData(response.data);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError("Erreur lors du chargement des données");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <LoadingSpinner message="Chargement des données..." />;
  }

  if (error) {
    return <ErrorMessage error={error} />;
  }

// Extraction de named exports via .then()
const LazyBarChart = lazy(() =>
  import('recharts').then(module => ({ default: module.BarChart }))
);
const LazyPieChart = lazy(() =>
  import('recharts').then(module => ({ default: module.PieChart }))
);

// Créer un composant mémoïsé
const MemoizedFileRow = React.memo(({ file }) => (
  <tr className="text-center">
    <td className="p-2 border border-gray-700">{file.name}</td>
    <td className="p-2 border border-gray-700">{file.droppedBy}</td>
    <td className={`p-2 border border-gray-700 ${file.score >= 70 ? "text-green-800" : "text-red-800"}`}>
      {file.score.toFixed(1)}%
    </td>
  </tr>
));

  return (
  <div className="p-6 w-full  pt-[50px] min-h-screen text-white ">
      <h1 className="text-2xl font-bold mb-6">📊 Dashboard de conformité</h1>

      {/* Cartes des indicateurs clés */}
      <div className="grid grid-cols-3 gap-6">
        {[{
          title: "Total fichiers analysés",
          value: data?.totalFiles ?? 0,
          color: "text-blue-400"
        }, {
          title: "Total lignes analysées",
          value: data?.totalRows ?? 0,
          color: "text-green-400"
        }, {
          title: "Moyenne valeurs manquantes",
          value: `${(data?.avgMissingPercentage ?? 0).toFixed(2)}%`,
          color: "text-red-400"
        }].map((item, index) => (
          <div key={index} className="bg-transparent p-4 shadow-lg shadow-white/75 rounded-xl">
            <h2 className="text-lg font-semibold">{item.title}</h2>
            <p className={`text-3xl ${item.color}`}>{item.value}</p>
          </div>
        ))}
      </div>

      {/* Graphiques */}
      <div className="grid grid-cols-2 gap-6 mt-[70px] mb-[70px]">
      <Suspense fallback={<div className="text-white">Chargement des graphiques...</div>}>

        <div className="bg-transparent p-4 shadow-lg shadow-white/75

 rounded-xl">
          <h2 className="text-lg font-semibold">Scores de conformité</h2>

          <ResponsiveContainer width="100%" height={300}>
            <LazyBarChart data={data?.conformityScores ?? []}>
              <CartesianGrid strokeDasharray="3 3" stroke="#ffffff55" />
              <XAxis dataKey="name" stroke="#fff" />
              <YAxis stroke="#fff" />
              <Tooltip contentStyle={{ backgroundColor: "#333", color: "#fff" }} />
              <Legend wrapperStyle={{ color: "#fff" }} />
              <Bar dataKey="score" fill="#82ca9d" />
            </LazyBarChart>
          </ResponsiveContainer>
        </div>

        <div className="bg-transparent p-4 shadow-lg shadow-white/75 rounded-xl">
          <h2 className="text-lg font-semibold">% de chaque type de fichier</h2>
          <ResponsiveContainer width="100%" height={300}>
            <LazyPieChart>
              <Pie data={data?.fileTypes ?? []} dataKey="count" nameKey="type" cx="50%" cy="50%" outerRadius={100} label>
                {data?.fileTypes?.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip contentStyle={{ backgroundColor: "#333", color: "#fff" }} />
            </LazyPieChart>
          </ResponsiveContainer>
        </div>

        </Suspense>
      </div>

      {/* Liste des fichiers récents */}
      <div className="bg-transparent p-4 

 rounded-xl mt-6">
        <h2 className="text-lg font-semibold">Derniers fichiers analysés</h2>
        <div className="overflow-x-auto shadow-lg shadow-white/75">
          <table className="w-full mt-4 border border-gray-700 ">
            <thead>
              <tr className="shadow-xl text-gray-400 shadow-black/30">
                <th className="p-2 border border-gray-700">Fichier</th>
                <th className="p-2 border border-gray-700">Organisation</th>
                <th className="p-2 border border-gray-700">Conformité</th>
              </tr>
            </thead>
            <tbody>
              {data?.conformityScores?.map((file, index) => (
              <MemoizedFileRow key={`${file.name}-${index}`} file={file} />
))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Pagination */}
      <div className="flex justify-center mt-4 ">
        {Array.from({ length: totalPages }, (_, i) => (
          <button
            key={i}
            onClick={() => handlePageChange(i)}
      
            className={`mx-1 px-3 py-1 z-40 rounded ${i === page ? 'bg-blue-500' : 'bg-gray-700'}`}
          >
            {i + 1}
          </button>
        ))}
      </div>
    </div>

  
  );
};

export default Dashboard;
