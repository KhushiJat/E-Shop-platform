import { Swiper, SwiperSlide } from 'swiper/react';
import 'swiper/css/navigation';
import 'swiper/css/pagination';
import 'swiper/css/scrollbar';
import 'swiper/css/effect-fade';
import 'swiper/css/autoplay';

// Import Swiper styles
import 'swiper/css';
import { Autoplay, Pagination, EffectFade, Navigation } from 'swiper/modules';

import { bannerLists } from '../../utils';
import { Link } from 'react-router-dom';

const colors = ["bg-indigo-950", "bg-slate-900", "bg--800"];

// const colors = ["bg-banner-color1", "bg-banner-color2", "bg-banner-color3"];

const HeroBanner = () => {
    return (
        <div className='py-2 rounded-md'>
            <Swiper
                grabCursor={true}
                autoplay={{
                    delay: 4000,
                    disableOnInteraction: false,
                }}
                navigation
                modules={[Pagination, EffectFade, Navigation, Autoplay]}
                pagination={{ clickable: true }}
                scrollbar={{ draggable: true }}
                slidesPerView={1}>

                 {bannerLists.map((item, i) => (
    <SwiperSlide key={item.id}>
        <div 
            className="carousel-item rounded-md sm:h-[500px] h-96 relative bg-cover bg-center"
            style={{ 
                backgroundImage: `linear-gradient(rgba(15, 23, 42, 0.8), rgba(15, 23, 42, 0.8)), url(${item.image})` 
            }}
        >
            {/* Main container with max-width and centered automatically */}
            <div className='max-w-7xl mx-auto h-full flex flex-col justify-center items-start px-6 sm:px-12 lg:px-16 relative z-10'>
                
                {/* Text Box */}
                <div className='w-full lg:w-1/2 flex flex-col items-start text-left py-4'>
                    <h3 className='text-xl sm:text-2xl lg:text-3xl text-white font-semibold tracking-wide drop-shadow-md'>
                        {item.title}
                    </h3>
                    <h1 className='text-3xl sm:text-4xl lg:text-6xl text-white font-extrabold mt-2 leading-tight drop-shadow-md'>
                        {item.subtitle}
                    </h1>
                    <p className='text-slate-200 text-sm sm:text-base font-medium mt-3 max-w-md drop-shadow'>
                        {item.description}
                    </p>
                    <Link 
                        className='mt-6 inline-block bg-white text-slate-900 font-semibold py-3 px-8 rounded shadow-lg hover:bg-gray-100 transition-all duration-300'
                        to="/products">
                    Shop Now
                    </Link>
                </div>

            </div>
        </div>
    </SwiperSlide>
))}

            </Swiper>
        </div>
    );
}

export default HeroBanner;