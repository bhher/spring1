import { Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import ListPage from './pages/ListPage.jsx'
import DetailPage from './pages/DetailPage.jsx'
import AddPage from './pages/AddPage.jsx'
import EditPage from './pages/EditPage.jsx'

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<ListPage />} />
        <Route path="/list" element={<ListPage />} />
        <Route path="/list/:num" element={<DetailPage />} />
        <Route path="/list/:num/edit" element={<EditPage />} />
        <Route path="/mains/add" element={<AddPage />} />
      </Routes>
    </Layout>
  )
}
